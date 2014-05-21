package com.hp.it.perf.ac.app.hpsc.realtime;

import java.util.Map;

import com.hp.it.perf.ac.common.realtime.MessageBean;
import com.hp.it.perf.ac.common.realtime.RealTimeBean;

// notification send ac common data (not payload)
public interface RealtimeDataProxyMXBean {

	public long getProxyedDataCount();

	public long getProxyedBatchCount();
	
	public void addData(RealTimeBean ... datas);
	
	public void addErrorMessageData(MessageBean ... messageData);
	
	public void deleteOldDataByGranularityAndStartTime();
	
	public void updateGruanularityLatestStartTime(Map<Integer, Long> granularityLatestStartTime);

}
