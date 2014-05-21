package com.hp.it.perf.ac.app.hpsc.realtime.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.hp.it.perf.ac.common.realtime.RealTimeBean;

public interface RealtimeRepository extends
		PagingAndSortingRepository<RealTimeBean, String> {

	public Iterable<RealTimeBean> findByGranularity(int granularity);

	public Iterable<RealTimeBean> findByValueType(int valueType);

	public Iterable<RealTimeBean> findByCategoryAndFeatureType(int category,
			int featureType);

	public Iterable<RealTimeBean> findByGranularityAndStartTimeLessThan(
			int granularity, long startTime);
	
	public Page<RealTimeBean> findByGranularityAndValueTypeAndStartTimeGreaterThan(
			int granularity, int valueType, long startTime, Pageable pageable);

	public Page<RealTimeBean> findByGranularityAndValueTypeAndCategoryAndFeatureTypeAndStartTimeGreaterThan(
			int granularity, int valueType, int category, int featureType,
			long startTime, Pageable pageable);
	
	public Page<RealTimeBean> findByGranularityAndValueTypeAndCategoryAndStartTimeGreaterThan(
			int granularity, int valueType, int category, long startTime, Pageable pageable);
}
