package com.hp.it.perf.ac.app.hpsc.storm.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.hp.it.perf.ac.app.hpsc.storm.trident.AcCommonDataTrident.AcUpdaterImp;
import com.hp.it.perf.ac.common.realtime.GranularityType;

import storm.trident.state.State;
import storm.trident.state.StateFactory;
import backtype.storm.task.IMetricsContext;

public class MemoryDBFactory implements StateFactory {
	private static final long serialVersionUID = 2555160307652767177L;
	
	String id;
	Map<List<Object>, Object> db = null;
	
	List<String> otherIds;
	GranularityType granularityType;
	private boolean scheduleFlag = false;

	public MemoryDBFactory() {
		this.id = UUID.randomUUID().toString();
	}
	
	public MemoryDBFactory(List<String> otherIds, GranularityType granularityType, boolean scheduleFlag) {
		this.id = UUID.randomUUID().toString();
		this.otherIds = otherIds;
		this.granularityType = granularityType;
		this.scheduleFlag = scheduleFlag;
	}
	
	public MemoryDBFactory(Map<List<Object>, Object> db) {
		this.id = UUID.randomUUID().toString();
		this.db = db;
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
		if(db == null) {
			if(scheduleFlag && otherIds != null && otherIds.size() == 2) {
				List<String> ids = new ArrayList<String>(otherIds);
				ids.add(id);
				long currenMils = System.currentTimeMillis();
				long granularityMils = granularityType.getMilSecondTime();
				long periodMilSeconds = granularityMils;			
				long initialDelayMilSeconds = currenMils / granularityMils * granularityMils + granularityMils - currenMils + Consts.TURN_OFF_MILLISECCONDS + 1000;
				MemoryDBState.scheduleUpdate(
						new AcUpdaterImp(granularityType.getMilSecondTime()
								+ Consts.TURN_OFF_MILLISECCONDS,
								granularityType, 1), JMXUpdateDelegate.getInstance(), initialDelayMilSeconds, periodMilSeconds, ids.toArray(new String[ids.size()]), new int[]{1, 6, 2});
				
			}
			return new MemoryDBState(this.id);
		} else {
			return new MemoryDBState(this.id, db);
		}
	}
	
	public String getId() {
		return id;
	}

}
