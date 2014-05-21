package com.hp.it.perf.ac.app.hpsc.storm.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.hp.it.perf.ac.app.hpsc.storm.beans.RawDataBean;
import com.hp.it.perf.ac.app.hpsc.storm.util.JMXDataReceiver.RealtimeDataProxy;
import com.hp.it.perf.ac.common.realtime.MessageBean;
import com.hp.it.perf.ac.common.realtime.RealTimeBean;
import com.hp.it.perf.ac.common.realtime.ValueType;

public class JMXUpdateDelegate implements IUpdaterDelegate<RawDataBean>, JMXUpdateDelegateMXBean, Serializable {
	
	private static final long serialVersionUID = 2087629616221087877L;

	private static final Logger logger = Logger.getLogger(JMXUpdateDelegate.class);
	
	private RealtimeDataProxy rtProxy;
	
	private String jmxURL;
	
	// store the latest startTime for each granularity
	private static Map<Integer, Long> granularityLatestStartTime = new HashMap<Integer, Long>();;
	
	private volatile long totalCount;
	
	private volatile long rawCount;
	
	private TimerTask timerTask = null;
	
	private Timer timer = new Timer();
	
	private static ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
	
	{
		threadPool.scheduleAtFixedRate(new Runnable(){

			@Override
			public void run() {
				if(rtProxy != null) {
					try {
						rtProxy.deleteOldDataByGranularityAndStartTime();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} 
				
			}}, 10, 1440, TimeUnit.MINUTES);
	}

	// cache the data if JMX connection is broken
	private volatile List<RealTimeBean> dataList = new ArrayList<RealTimeBean>();
	private volatile List<MessageBean> messageDataList = new ArrayList<MessageBean>();
	
	private static JMXUpdateDelegate instance = new JMXUpdateDelegate(Consts.getJMXUrl());
	
	//public JMXUpdateDelegate(){}
	
	private JMXUpdateDelegate(String jmxURL) {
		this.jmxURL = jmxURL;
		if(jmxURL != null) {
			rtProxy = JMXDataReceiver.getProxyByJMXURL(RealtimeDataProxy.class, jmxURL);
		}
		logger.debug("Construct the JMX update delegate");
	}
	
	public static JMXUpdateDelegate getInstance() {
		return instance;
	}
	
	@Override
	public synchronized void update(List<RawDataBean> data) {
		if(data != null && data.size() > 0) {
			rawCount += data.size();
			List<RealTimeBean> listData = new ArrayList<RealTimeBean>(data.size()*3);
			List<RawDataBean> wsrpErrorData = new ArrayList<RawDataBean>();
			List<MessageBean> messageData = new ArrayList<MessageBean>();
			boolean updateLatestStartTime = false;
			for(RawDataBean rawData : data) {
				rawData.getGruanlityType();
				int gruanlityType = rawData.getGruanlityType();
				long startTime = rawData.getStartTime();
				if(rawData.getCategory() == 6) {
					wsrpErrorData.add(rawData);
					/*messageData.add(new MessageBean(gruanlityType,
							rawData.getCategory(), rawData.getType(), startTime, rawData.getMessage(),
							rawData.getTotalCount()));*/
					// Change category to 1 and featureType to 1
					messageData.add(new MessageBean(gruanlityType,
							1, 1, startTime, rawData.getMessage(),
							rawData.getTotalCount()));
				} else {
					RealTimeBean beanTotalCount = new RealTimeBean(
							gruanlityType, rawData.getCategory(), rawData.getType(),
							ValueType.TotalCount.getIndex(),
							startTime, rawData.getTotalCount());
					RealTimeBean beanErrorCount = new RealTimeBean(
							gruanlityType, rawData.getCategory(), rawData.getType(),
							ValueType.ErrorCount.getIndex(),
							startTime, rawData.getErrorCount());
					RealTimeBean beanScore = new RealTimeBean(
							gruanlityType, rawData.getCategory(), rawData.getType(),
							ValueType.Score.getIndex(),
							startTime, rawData.getScore());
					listData.add(beanTotalCount);
					if(rawData.getCategory() != 1) {
						listData.add(beanErrorCount);
					}
					listData.add(beanScore);
				}
				
				if(!granularityLatestStartTime.containsKey(gruanlityType) || (granularityLatestStartTime.get(gruanlityType) < startTime)) {
					granularityLatestStartTime.put(gruanlityType, startTime);
					updateLatestStartTime = true;
				} 
			}
			if(wsrpErrorData.size() > 0 ) {
				List<RealTimeBean> mergeWSRPErrorData = mergeWSRPErrorData(wsrpErrorData);
				if(mergeWSRPErrorData != null && mergeWSRPErrorData.size() > 0) {
					listData.addAll(mergeWSRPErrorData);
				}
			}
			totalCount += listData.size();
			
			if(rtProxy != null) {
				if(timerTask != null) {
					timer.cancel();
					timer.purge();
					timerTask = null;
				}
				try {
					// send realtime bean data by rtProxy
					if (listData.size() > 0) {
						rtProxy.addData(listData
								.toArray(new RealTimeBean[listData.size()]));
					}
					if (dataList.size() > 0) {
						rtProxy.addData(dataList
								.toArray(new RealTimeBean[dataList.size()]));
						dataList.clear();
					}

					// send message bean by rtProxy
					if (messageData.size() > 0) {
						rtProxy.addErrorMessageData(messageData
								.toArray(new MessageBean[messageData.size()]));
					}
					if(messageDataList.size() > 0) {
						rtProxy.addErrorMessageData(messageDataList
								.toArray(new MessageBean[messageDataList.size()]));
						messageDataList.clear();
					}

					// send latest start time for each granularity by rtProxy
					if (updateLatestStartTime) {
						rtProxy.updateGruanularityLatestStartTime(granularityLatestStartTime);
					}
				} catch(IOException e) {
					logger.error("Catching IOException when JMX connection is unavailable");
					dataList.addAll(listData);
					messageDataList.addAll(messageData);
					rtProxy = null;
					if(timerTask == null) {
						timerTask = new ReconnectJMXTimerTask();
					}
					logger.info("Schedule a task to reconnect JMX every 10 minutes");
					timer.scheduleAtFixedRate(timerTask, 0L, TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES));
				}
			} else {
				dataList.addAll(listData);
				messageDataList.addAll(messageDataList);
				if(dataList.size() > Consts.MAX_CACHE_SIZE) {
					logger.info("Clear the cache data as the data size exceeds the maximum size");
					dataList.clear(); 
					messageDataList.clear();
				}
				
			}
			
		}
	}
	
	
	// merge the error data which has same start time, but it has not same message
	private List<RealTimeBean> mergeWSRPErrorData(List<RawDataBean> wsrpErrorData) {
		if(wsrpErrorData == null || wsrpErrorData.size() == 0) {
			return null;
		}
		List<RealTimeBean> listData = new ArrayList<RealTimeBean>(wsrpErrorData.size());
		Map<Long, RealTimeBean> mapData = new HashMap<Long, RealTimeBean>();
		for(RawDataBean rawData : wsrpErrorData) {
			RealTimeBean bean;
			long timeStart = rawData.getStartTime();
			if(mapData.containsKey(timeStart)) {
				bean = mapData.get(timeStart);
				bean.setValue(bean.getValue() + rawData.getErrorCount());
			} else {
				bean = new RealTimeBean(rawData.getGruanlityType(), 1, 1,
						ValueType.ErrorCount.getIndex(),
						timeStart, rawData.getErrorCount());
				mapData.put(timeStart, bean);
			}
		}
		listData.addAll(mapData.values());
		return listData;
		
	}

	@Override
	public long getSize() {
		return totalCount;
	}

	@Override
	public long getRawSize() {
		return rawCount;
	}

	private class ReconnectJMXTimerTask extends TimerTask {

		@Override
		public void run() {
			if(rtProxy == null) {
				logger.debug("Reconnect JMX in the timer task");
				rtProxy = JMXDataReceiver.getProxyByJMXURL(RealtimeDataProxy.class, jmxURL);
			}
		}
		
	}
	
}
