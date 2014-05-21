package com.hp.it.perf.ac.app.hpsc.realtime;

import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.it.perf.ac.app.hpsc.realtime.persistence.MessageBeanRepository;
import com.hp.it.perf.ac.app.hpsc.realtime.persistence.RealtimeRepository;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.common.realtime.GranularityType;
import com.hp.it.perf.ac.common.realtime.MessageBean;
import com.hp.it.perf.ac.common.realtime.RealTimeBean;
import com.hp.it.perf.ac.common.realtime.RealTimeIdHelper;
import com.hp.it.perf.ac.core.AcDataSubscriber;
import com.hp.it.perf.ac.core.AcStatusSubscriber;
import com.hp.it.perf.ac.core.AcStatusSubscriber.Status;
import com.hp.it.perf.ac.core.service.AcServiceConfig;

@Service
@Transactional
class RealtimeServiceImpl implements RealtimeService {

	private volatile RealtimeDataProxy proxy;

	@Inject
	private AcServiceConfig serviceConfig;

	@Inject
	private RealtimeRepository realtimeRepository;

	@Inject
	private MessageBeanRepository messageRepository;

	@AcDataSubscriber(maxBufferSize = 100)
	public void onData(AcCommonDataWithPayLoad... data) {
		RealtimeDataProxy dataProxy = proxy;
		if (dataProxy != null) {
			dataProxy.sendCommonData(data);
		}
	}

	@AcStatusSubscriber(Status.ACTIVE)
	public void onActive() {
		proxy = new RealtimeDataProxy(this, serviceConfig.getCoreContext()
				.getSession());
		try {
			ManagementFactory.getPlatformMBeanServer().registerMBean(proxy,
					proxy.getObjectName());
		} catch (InstanceAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AcStatusSubscriber(Status.DEACTIVE)
	public void onDeactive() {
		try {
			ManagementFactory.getPlatformMBeanServer().unregisterMBean(
					proxy.getObjectName());
		} catch (MBeanRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		proxy = null;
	}

	@Override
	public RealTimeBean getDataById(String id) {
		return realtimeRepository.findOne(id);
	}

	@Override
	public Collection<RealTimeBean> getAllRealtimeData() {
		return (Collection<RealTimeBean>) realtimeRepository.findAll();
	}

	@Override
	public Collection<RealTimeBean> getDataByGranularity(
			GranularityType granularity) {
		return (Collection<RealTimeBean>) realtimeRepository
				.findByGranularity(granularity.getIndex());
	}

	@Override
	public Collection<RealTimeBean> getDataByValueType(int valueType) {
		return (Collection<RealTimeBean>) realtimeRepository
				.findByValueType(valueType);
	}

	@Override
	public Collection<RealTimeBean> getDataByCategoryAndFeatureType(
			int category, int featureType) {
		return (Collection<RealTimeBean>) realtimeRepository
				.findByCategoryAndFeatureType(category, featureType);
	}

	@Override
	public void addData(Iterable<RealTimeBean> datas) {
		realtimeRepository.save(datas);
	}

	@Override
	public void addData(RealTimeBean data) {
		realtimeRepository.save(data);
	}

	@Override
	public void addMessageData(Iterable<MessageBean> messageData) {
		messageRepository.save(messageData);

	}

	@Override
	public void deleteData(RealTimeBean data) {
		realtimeRepository.delete(data);
	}

	@Override
	public void deleteData(String id) {
		realtimeRepository.delete(id);
	}

	@Override
	public void deleteData(Iterable<RealTimeBean> datas) {
		realtimeRepository.delete(datas);
	}

	@Override
	public void deleteAll() {
		realtimeRepository.deleteAll();
	}

	@Override
	public long getSize() {
		return realtimeRepository.count();
	}

	@Override
	public void deleteByGranularityAndStartTimeLessThan(int granularity,
			long startTime) {
		Iterable<RealTimeBean> iterable = realtimeRepository
				.findByGranularityAndStartTimeLessThan(granularity, startTime);
		if (iterable != null && iterable.iterator() != null
				&& iterable.iterator().hasNext()) {
			realtimeRepository.delete(iterable);
		}

	}

	@Override
	public Page<RealTimeBean> getDataByGranularityAndValueTypeAndStartTimeGreaterThan(
			int granularity, int valueType, long startTime, Pageable pageable) {
		return realtimeRepository
				.findByGranularityAndValueTypeAndStartTimeGreaterThan(
						granularity, valueType, startTime, pageable);
	}

	@Override
	public Page<RealTimeBean> getByGranularityAndValueTypeAndCategoryAndFeatureTypeAndStartTimeGreaterThan(
			int granularity, int valueType, int category, int featureType,
			long startTime, Pageable pageable) {
		return realtimeRepository
				.findByGranularityAndValueTypeAndCategoryAndFeatureTypeAndStartTimeGreaterThan(
						granularity, valueType, category, featureType,
						startTime, pageable);
	}

	public void saveLatestScore(RealTimeBean value) {
		if (value != null) {
			// id is generated by "valueType", "granulityType", "category",
			// "featureType", can use the help class RealTimeIdHelper to
			// generate it.
			ScoreStore.put(
					RealTimeIdHelper.getInstance().getId(value.getValueType(),
							value.getGranularity(), value.getCategory(),
							value.getFeatureType()), value);
		}
	}

	public RealTimeBean getLatestScore(int id) {
		return ScoreStore.get(id);
	}

	public void updateGruanularityLatestStartTime(
			Map<Integer, Long> granularityLatestStartTime) {
		ScoreStore.updateLatestStartTime(granularityLatestStartTime);
	}

	@Override
	public Long getGruanularityLatestStartTime(int granularity) {
		return ScoreStore.getLatestStartTime(granularity);
	}

	@Override
	public Map<Integer, RealTimeBean> getAllScore() {
		return ScoreStore.getAllScore();
	}

	@Override
	public Map<Integer, Long> getAllStartTime() {
		return ScoreStore.getAllStartTime();
	}

	@Override
	public Page<MessageBean> getByGranularityAndCategoryAndFeatureTypeAndStartTimeGreaterThan(
			int granularity, int category, int featureType, long startTime,
			Pageable pageable) {
		return messageRepository
				.findByGranularityAndCategoryAndFeatureTypeAndStartTimeGreaterThan(
						granularity, category, featureType, startTime, pageable);
	}

	@Override
	public Page<RealTimeBean> getByGranularityAndValueTypeAndCategoryAndStartTimeGreaterThan(
			int granularity, int valueType, int category, long startTime,
			Pageable pageable) {
		return realtimeRepository.findByGranularityAndValueTypeAndCategoryAndStartTimeGreaterThan(
				granularity, valueType, category, startTime, pageable);
	}
}
