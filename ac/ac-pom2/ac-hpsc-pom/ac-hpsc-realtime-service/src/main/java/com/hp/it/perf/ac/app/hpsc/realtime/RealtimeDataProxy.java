package com.hp.it.perf.ac.app.hpsc.realtime;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MBeanNotificationInfo;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.common.realtime.GranularityType;
import com.hp.it.perf.ac.common.realtime.MessageBean;
import com.hp.it.perf.ac.common.realtime.RealTimeBean;

public class RealtimeDataProxy extends NotificationBroadcasterSupport implements
		RealtimeDataProxyMXBean, NotificationEmitter {

	private ObjectName objectName;

	private AtomicLong dataCount = new AtomicLong();

	private AtomicLong batchCount = new AtomicLong();

	private AtomicLong seq = new AtomicLong();

	private RealtimeServiceImpl realtimeService;
	
	private static final int KEEP_DATA_MAX_COUNT = 360;

	RealtimeDataProxy(RealtimeServiceImpl realtimeService, AcSession session) {
		super(new MBeanNotificationInfo(new String[] { "AC_DATA" }, "AC_DATA",
				null));
		this.realtimeService = realtimeService;
		try {
			objectName = ObjectName.getInstance(RealtimeDataProxy.class
					.getPackage().getName()
					+ ":type=RealtimeDataProxy,sessionId="
					+ session.getSessionId());
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(
					"create realtime data proxy object name error", e);
		}
	}

	ObjectName getObjectName() {
		return objectName;
	}

	void sendCommonData(AcCommonDataWithPayLoad... data) {
		Notification notification = new Notification("AC_DATA", objectName,
				seq.incrementAndGet());
		AcCommonData[] array = new AcCommonData[data.length];
		for (int i = 0; i < array.length; i++) {
			array[i] = new AcCommonData(data[i]);
		}
		notification.setUserData(NotificationInfoSerializer
				.serializeLineRecord(array));
		sendNotification(notification);
		batchCount.incrementAndGet();
		dataCount.addAndGet(array.length);
	}

	@Override
	public long getProxyedDataCount() {
		return dataCount.get();
	}

	@Override
	public long getProxyedBatchCount() {
		return batchCount.get();
	}

	public static interface NotificationInfoSerializerInterface {
		public AcCommonData[] getAcCommonDataArray();

		public void setAcCommonDataArray(AcCommonData[] data);
	}

	public static class NotificationInfoSerializer extends StandardMBean
			implements NotificationInfoSerializerInterface {

		private AcCommonData[] data;

		private static NotificationInfoSerializer me = new NotificationInfoSerializer();

		public NotificationInfoSerializer() {
			super(NotificationInfoSerializerInterface.class, true);
		}

		public AcCommonData[] getAcCommonDataArray() {
			return data;
		}

		public void setAcCommonDataArray(AcCommonData[] data) {
			this.data = data;
		}

		public static Object serializeLineRecord(AcCommonData[] data) {
			try {
				me.setAcCommonDataArray(data);
				return me.getAttribute("AcCommonDataArray");
			} catch (Exception ex) {
				throw new RuntimeException("Unexpected exception", ex);
			}
		}
	}

	@Override
	public void addData(RealTimeBean... datas) {
		realtimeService.addData(Arrays.asList(datas));
		for(RealTimeBean data : datas) {
			realtimeService.saveLatestScore(data);
		}
	}
	
	public void deleteOldDataByGranularityAndStartTime() {
		List<GranularityType> granularityTypeList = GranularityType.getGranularityTypeList();
		long currentTimeMillis = System.currentTimeMillis();
		for(GranularityType granularityType : granularityTypeList) {
			long deleteStartTime = currentTimeMillis
					- granularityType.getMilSecondTime()
					* (KEEP_DATA_MAX_COUNT + 1);
			realtimeService.deleteByGranularityAndStartTimeLessThan(granularityType.getIndex(), deleteStartTime);
		}
	}

	@Override
	public void addErrorMessageData(MessageBean... messageData) {
		realtimeService.addMessageData(Arrays.asList(messageData));
		
	}
	
	public void updateGruanularityLatestStartTime(Map<Integer, Long> granularityLatestStartTime) {
		realtimeService.updateGruanularityLatestStartTime(granularityLatestStartTime);
	}
}
