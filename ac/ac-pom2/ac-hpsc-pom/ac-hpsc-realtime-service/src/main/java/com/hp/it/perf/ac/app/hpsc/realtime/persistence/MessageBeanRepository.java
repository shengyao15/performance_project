package com.hp.it.perf.ac.app.hpsc.realtime.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.hp.it.perf.ac.common.realtime.MessageBean;

public interface MessageBeanRepository extends
		PagingAndSortingRepository<MessageBean, String> {

	public Iterable<MessageBean> findByGranularity(int granularity);

	public Iterable<MessageBean> findByCategoryAndFeatureType(int category,
			int featureType);

	public Iterable<MessageBean> findByGranularityAndStartTimeLessThan(
			int granularity, long startTime);
	
	public Iterable<MessageBean> findByGranularityAndMessage(
			int granularity, String message);
	
	public Page<MessageBean> findByGranularityAndStartTimeGreaterThan(
			int granularity, long startTime, Pageable pageable);

	public Page<MessageBean> findByGranularityAndCategoryAndFeatureTypeAndStartTimeGreaterThan(
			int granularity, int category, int featureType,
			long startTime, Pageable pageable);
}
