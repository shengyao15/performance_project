package com.hp.it.perf.ac.app.hpsc.storm.trident;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hp.it.perf.ac.app.hpsc.storm.beans.RawDataBean;
import com.hp.it.perf.ac.app.hpsc.storm.beans.SumCountErrorBean;
import com.hp.it.perf.ac.app.hpsc.storm.function.AcKeepLogFilter;
import com.hp.it.perf.ac.app.hpsc.storm.function.AcPortalBizFilter;
import com.hp.it.perf.ac.app.hpsc.storm.function.AcSPFDetailErrorFilter;
import com.hp.it.perf.ac.app.hpsc.storm.function.AcSPFFilter;
import com.hp.it.perf.ac.app.hpsc.storm.function.ConvertDurationToScore;
import com.hp.it.perf.ac.app.hpsc.storm.function.ConvertLevelToErrorStatus;
import com.hp.it.perf.ac.app.hpsc.storm.function.SplitByTimeSlice;
import com.hp.it.perf.ac.app.hpsc.storm.function.SumCountErrorAgg;
import com.hp.it.perf.ac.app.hpsc.storm.spout.AcCommonDataReceiverSpout;
import com.hp.it.perf.ac.app.hpsc.storm.util.IUpdater;
import com.hp.it.perf.ac.app.hpsc.storm.util.IUpdaterDelegate;
import com.hp.it.perf.ac.app.hpsc.storm.util.MemoryDBFactory;
import com.hp.it.perf.ac.common.realtime.GranularityType;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Count;
import storm.trident.state.OpaqueValue;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;

public class AcCommonDataTrident {
	
	public static StormTopology buildTopology(AcCommonDataReceiverSpout spout) {
	       
        TridentTopology topology = new TridentTopology();  
        
        Stream streamFromSpout = topology.newStream("spout", spout).parallelismHint(1);
        
        List<GranularityType> granularityList = GranularityType.getGranularityTypeList();
		String[] fields = new String[] { "minute", "tenMinutes",
				"fifteenMinutes", "thirtyMinutes", "hour", "day" };
 
		Stream timeSpliceStream = streamFromSpout.each(new Fields("category"), new AcKeepLogFilter())
		.each(new Fields("category", "level"), new ConvertLevelToErrorStatus(), new Fields("errorStatus"))
		.each(new Fields("duration", "hasNoWSRP"), new ConvertDurationToScore(), new Fields("score", "validatedScore"))
        .each(new Fields("created"), new SplitByTimeSlice(granularityList), new Fields(fields));
		
		for(int i = 0; i < granularityList.size(); i++) {
			GranularityType granularityType = granularityList.get(i);
			List<String> spfIds = spfStorm(timeSpliceStream, fields[i]);
		
			bizStorm(timeSpliceStream, fields[i], spfIds, granularityType);
			
		}
		
        return topology.build();
    }
	
	private static List<String> spfStorm(Stream stream, String startTimeField) {
		List<String> dbIds = new ArrayList<String>();
		// do request total count and score aggregate
		MemoryDBFactory stateFactory = new MemoryDBFactory();
		String spfDbId = stateFactory.getId();
		dbIds.add(spfDbId);
		Stream spfStream = stream.each(new Fields("category"), new AcSPFFilter());
		spfStream.groupBy(new Fields(startTimeField))
        .persistentAggregate(stateFactory, new Fields("score", "errorStatus", "validatedScore"), new SumCountErrorAgg(), new Fields("sumCount"))
        .parallelismHint(1);
		
		// do request error count aggregate
		MemoryDBFactory stateFactoryDetail = new MemoryDBFactory();
		String spfDetailDbId = stateFactoryDetail.getId();
		dbIds.add(spfDetailDbId);
		Stream spfDetailStream = stream.each(new Fields("category", "level"), new AcSPFDetailErrorFilter());
		spfDetailStream.groupBy(new Fields(startTimeField, "errorMessage"))
		.persistentAggregate(stateFactoryDetail, new Count(), new Fields("errorCount"))
		.parallelismHint(1);

		return dbIds;
	}
	
	private static List<String> bizStorm(Stream stream, String startTimeField, List<String> spfIds, GranularityType granularityType) {
		List<String> dbIds = new ArrayList<String>();
		
		MemoryDBFactory stateFactory = new MemoryDBFactory(spfIds, granularityType, true);
		String id = stateFactory.getId();
		dbIds.add(id);
		Stream bizStream = stream.each(new Fields("category"), new AcPortalBizFilter());
		bizStream.groupBy(new Fields(startTimeField, "type"))
        .persistentAggregate(stateFactory, new Fields("score", "errorStatus"), new SumCountErrorAgg(), new Fields("sumCountError"))
        .parallelismHint(1);
		
		return dbIds;
		
	}
	
	public static class AcUpdaterImp implements IUpdater<List<Object>, Object> {
		
		private long compareTimeMilSeconds;
		
		private long granularityTimeSlice;
		
		private GranularityType granularityType;
		
		private int category;
		
		public AcUpdaterImp(long compareTimeMilSeconds, GranularityType granularityType, int category) {
			this.compareTimeMilSeconds = compareTimeMilSeconds;
			this.granularityTimeSlice = granularityType.getMilSecondTime();
			this.granularityType = granularityType;
			this.category = category;
		}

		@SuppressWarnings({ "rawtypes" })
		@Override
		public void updateByDelegate(IUpdaterDelegate delegate, Map<List<Object>, Object> db) {
			this.updateByDelegate(delegate, db, this.category);
		}
		
		private void updateLocalDBMap(Map<List<Object>, Object> db, List<List<Object>> keyList, boolean checkDB) {
			if(db == null || db.size() == 0|| keyList == null || keyList.size() == 0) {
				return;
			}
			for(List<Object> key : keyList) {
				if(checkDB) {
					if(db.containsKey(key)) {
						db.remove(key);
					}
				} else {
					db.remove(key);
				}
			}
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void updateByDelegate(IUpdaterDelegate delegate,
				Map<List<Object>, Object> db, int category) {
			if(db != null) {
				long currentMil = new Date().getTime();
				long lastToUpdateTime = currentMil - compareTimeMilSeconds;
				List<List<Object>> keys = new ArrayList<List<Object>>();
				List<RawDataBean> data = new ArrayList<RawDataBean>();
				for(List<Object> keyList : db.keySet()) {
					long startTime = ((Long)keyList.get(0)) * granularityTimeSlice;
					if(startTime < lastToUpdateTime) {
						keys.add(keyList);
						int type = 1;
						if(category == 2) {
							type = (Integer)keyList.get(1);
						}
						OpaqueValue<Object> value = (OpaqueValue<Object>) db.get(keyList);
						RawDataBean dataBean;
						if (category == 6) {
							String errorMessage = (String) keyList.get(1);
							Long errorCount = (Long) value.getCurr();
							dataBean = new RawDataBean(startTime,
									granularityType.getIndex(), category, type,
									errorCount.intValue(), errorCount.intValue(), 0);
							dataBean.setMessage(errorMessage);
						} else {
							SumCountErrorBean currBean = (SumCountErrorBean) value
									.getCurr();
							dataBean = new RawDataBean(startTime,
									granularityType.getIndex(), category, type,
									currBean.getCount(),
									currBean.getErrorCount(), currBean
											.getSum().doubleValue()
											/ currBean.getSumCount());
						}
						data.add(dataBean);
					}
				}
				updateLocalDBMap(db, keys, false);
				delegate.update(data);
			} else {
				// TODO log
			}
			
		}
	}
	
}
