package com.hp.it.perf.ac.common.core;

import com.hp.it.perf.ac.common.base.AcPredicate;

public interface AcDataHub<T> {

	public AcDataHubEndpoint<T> createDataEndpoint(AcDataListener<T> listener,
			int maxBatchSize, int maxWaitTime);

	public AcDataHubEndpoint<T> createDataEndpoint(AcDataListener<T> listener,
			int maxBatchSize, int maxWaitTime, AcPredicate<T> filter);

}
