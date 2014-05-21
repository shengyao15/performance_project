package com.hp.it.perf.ac.app.hpsc.storm.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcContext;
import com.hp.it.perf.ac.common.realtime.MessageBean;
import com.hp.it.perf.ac.common.realtime.RealTimeBean;

public class JMXDataReceiver implements NotificationListener {
	
	private static final Logger logger = Logger.getLogger(JMXDataReceiver.class);
	
	private LinkedBlockingQueue<AcCommonData[]> contentQueue = new LinkedBlockingQueue<AcCommonData[]>();
	
	private String jmxURL;
	
	private transient JMXConnector connector;
	
	private RealtimeDataProxy realtimeDataProxy;
	
	private long sleepMilSeconds = 0L;
	
	public JMXDataReceiver(String jmxURL) {
		this.jmxURL = jmxURL;
		tryConnectJMX(jmxURL);
	}
	
	public void tryConnectJMX(String jmxURL) {
		while(!connectJMX(jmxURL)) {
			if(sleepMilSeconds < Consts.JMX_RECONNECT_MAX_INTERVAL_MILLSECONDS) {
				sleepMilSeconds += Consts.JMX_RECONNECT_INCREASE_MILLSECONDS;
			}
			try {
				Thread.sleep(sleepMilSeconds);
			} catch (InterruptedException e) {
				logger.error("Interrupted Exception when sleep: ");
			}
			
		}
		sleepMilSeconds = 0L;
		logger.debug("JMX data receiver is started");
	}
	
	public boolean connectJMX(String jmxURL) {
		boolean start = false;
		try {
			JMXServiceURL monitorURL = new JMXServiceURL(
					JMXDataReceiver.this.jmxURL);
			connector = JMXConnectorFactory.connect(monitorURL);
			NotificationFilterSupport filterSupport = new NotificationFilterSupport();
			filterSupport.enableType(JMXConnectionNotification.CLOSED);
			connector.addConnectionNotificationListener(
					new NotificationListener() {

						@Override
						public void handleNotification(
								Notification notification, Object handback) {
							JMXDataReceiver jmxDataReceiver = (JMXDataReceiver) handback;
							jmxDataReceiver.realtimeDataProxy = null;
							jmxDataReceiver
									.tryConnectJMX(jmxDataReceiver.jmxURL);
						}
					}, filterSupport, this);
			MBeanServerConnection mbsc = connector.getMBeanServerConnection();
			logger.info("===> Connected to monitor server: " + monitorURL);
			Set<ObjectName> names = mbsc.queryNames(
					ObjectName.getInstance(Consts.OBJECT_NAME), null);
			if (names.isEmpty()) {
				throw new IOException("no monitor mbean found");
			}
			if (names.size() > 1) {
				throw new IOException("more monitor mbeans found: " + names);
			}
			ObjectName monitorObjName = names.iterator().next();
			realtimeDataProxy = JMX.newMXBeanProxy(mbsc, monitorObjName,
					RealtimeDataProxy.class, true);
			((NotificationEmitter) realtimeDataProxy).addNotificationListener(
					JMXDataReceiver.this, null, null);
			start = true;
			logger.debug("JMX connection starts successfully");
		} catch (Exception e) {
			logger.error("Error in running with JMX", e);
		}
		return start;
		
	}

	@Override
	public void handleNotification(Notification notification, Object handback) {
		Object userData = notification.getUserData();
		AcCommonData[] acCommonDatas = from((CompositeData [])userData);
		contentQueue.offer(acCommonDatas);

	}
	
	public static interface RealtimeDataProxy {

		public long getProxyedDataCount() throws IOException;

		public long getProxyedBatchCount() throws IOException;
		
		public void addData(RealTimeBean ... datas) throws IOException;
		
		public void addErrorMessageData(MessageBean ... messageData) throws IOException;
		
		public void deleteOldDataByGranularityAndStartTime() throws IOException;
		
		public void updateGruanularityLatestStartTime(Map<Integer, Long> granularityLatestStartTime) throws IOException;

	}
	
	public static AcCommonData[] from(CompositeData [] userData) {
		AcCommonData [] acDatas = new AcCommonData[userData.length];
		int i = 0;
		for(CompositeData data : userData) {
			AcCommonData acData = new AcCommonData();
			acData.setAcid((Long)data.get("acid"));
			acData.setName((String)data.get("name"));
			acData.setDuration((Integer)data.get("duration"));
			acData.setCreated((Long)data.get("created"));
			acData.setRefAcid((Long)data.get("refAcid"));
			CompositeData[] contextDatas = (CompositeData[])data.get("contexts");
			List<AcContext> contexts = new ArrayList<AcContext>(contextDatas.length);
			for(CompositeData contextData : contextDatas) {
				AcContext context = new AcContext();
				context.setCode((Integer)contextData.get("code"));
				context.setValue((String)contextData.get("value"));
				contexts.add(context);
			}
			acData.setContexts(contexts);
			acDatas[i++] = acData;
			
		}
		return acDatas;
	}
	
	public static <T> T getProxyByJMXURL(Class<T> proxyClass, String jmxURL) {
		if(jmxURL == null) {
			return null;
		}
		T proxy = null;
		
		try {
			JMXServiceURL monitorURL = new JMXServiceURL(jmxURL);
			JMXConnector connector = JMXConnectorFactory.connect(monitorURL);
			MBeanServerConnection mbsc = connector
					.getMBeanServerConnection();
			logger.info("===> Connected to monitor server: "
					+ monitorURL);
			Set<ObjectName> names = mbsc.queryNames(
					ObjectName.getInstance(Consts.OBJECT_NAME), null);
			if (names.isEmpty()) {
				throw new IOException("no monitor mbean found");
			}
			if (names.size() > 1) {
				throw new IOException("more monitor mbeans found: " + names);
			}
			ObjectName monitorObjName = names.iterator().next();
			proxy = JMX.newMXBeanProxy(mbsc, monitorObjName,
					proxyClass, false);
		} catch(Exception e) {
			logger.error("Error when getProxyByJMXURL. " + e.getMessage());
		}
		return proxy;
	}
	
	public RealtimeDataProxy getProxy() {
		return realtimeDataProxy;
	}
	
	public String getJmxURL() {
		return jmxURL;
	}
	
	public boolean closeJMX() {
		boolean action = false;
		realtimeDataProxy = null;
		if(connector != null) {
			try {
				connector.close();
				action = true;
			} catch (IOException e) {
				logger.error("IOException when close JMX connector.");
			}
		}
		return action;
	}
	
	public AcCommonData[] poll() {
		return contentQueue.poll();
		
	}
	
	public LinkedBlockingQueue<AcCommonData[]> getQueueData() {
		return contentQueue;
	}

}
