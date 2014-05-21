package com.hp.it.perf.ac.service.dispatch.impl;

import javax.inject.Inject;

import com.hp.it.perf.ac.common.core.AcDataListener;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.core.AcDispatchInfo;
import com.hp.it.perf.ac.core.AcDispatchRegistry;
import com.hp.it.perf.ac.core.AcStatusSubscriber;
import com.hp.it.perf.ac.core.AcStatusSubscriber.Status;
import com.hp.it.perf.ac.core.service.AcServiceConfig;
import com.hp.it.perf.ac.service.dispatch.AcDispatchService;

public class AcDispatchServiceImpl implements AcDispatchService {

	@Inject
	private AcInternalDataBus internalDataBus;

	@Inject
	private AcServiceConfig serviceConfig;

	@Override
	public void dispatch(AcCommonDataWithPayLoad... data) {
		internalDataBus.onData(data);
	}

	@Override
	public void registerDownstream(String name,
			AcDataListener<AcCommonDataWithPayLoad> downstream,
			AcDispatchInfo info) {
		if (info.getThreadCount() < 1) {
			throw new IllegalArgumentException("invalid thread count: "
					+ info.getThreadCount());
		}
		internalDataBus.registerDownstream(name, downstream,
				info.getQueueSize(), info.getMaxBufferSize(),
				info.getThreadCount());
	}

	@Override
	public void unregisterDownstream(String name) {
		// TODO
	}

	@Override
	public void closeDispatch() {
		internalDataBus.onDataFinished();
	}

	@AcStatusSubscriber(Status.ACTIVE)
	public void onActive() {
		AcDispatchRegistry registry = serviceConfig.getDispatchRegistry();
		for (String name : registry.getNames()) {
			AcDataListener<AcCommonDataWithPayLoad> downstream = registry
					.getDataListener(name);
			AcDispatchInfo info = registry.getDispatchInfo(name);
			registerDownstream(name, downstream, info);
		}
	}

	@AcStatusSubscriber(Status.DEACTIVE)
	public void onDeactive() {
		AcDispatchRegistry registry = serviceConfig.getDispatchRegistry();
		for (String name : registry.getNames()) {
			unregisterDownstream(name);
		}
	}

}
