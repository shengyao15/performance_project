package com.hp.it.perf.ac.app.hpsc.storm.function;

import java.util.ArrayList;
import java.util.List;

import com.hp.it.perf.ac.common.realtime.GranularityType;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * This class splits the created time to time slice
 * 
 * @author Qiu, Li-hong
 *
 */
public class SplitByTimeSlice extends BaseFunction{
	private static final long serialVersionUID = 2661018399958525879L;
	
	private List<GranularityType> granularityList;
	
	public SplitByTimeSlice() {
		
	}
	
	public SplitByTimeSlice(List<GranularityType> granularityList) {
		this.granularityList = granularityList;
	}

	@Override
	public void execute(TridentTuple tuple,
			TridentCollector collector) {
		Long cretedTime = tuple.getLong(0);
		if(granularityList != null && granularityList.size() > 0) {
			List <Object> valueList = new ArrayList<Object>(granularityList.size()*2);
			for(GranularityType gType : granularityList) {
				if(gType == null) {
					continue;
				}
				valueList.add(cretedTime / gType.getMilSecondTime());
			}
			/*long minuteTime = cretedTime / GranularityType.Minute.getMilSecondTime();
			collector.emit(new Values(minuteTime));*/
			collector.emit(new Values(valueList.toArray()));
		}
	}

}
