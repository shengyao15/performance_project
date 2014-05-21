package com.hp.it.perf.ac.service.dispatch.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.common.core.AcDataListener;
import com.hp.it.perf.ac.common.core.AcStatusEvent;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.common.model.AcidHelper;
import com.hp.it.perf.ac.core.AcCoreException;
import com.hp.it.perf.ac.core.AcDataStatusEvent;
import com.hp.it.perf.ac.core.AcStatusSubscriber.Status;
import com.hp.it.perf.ac.core.context.ListenerDataDispatcher;
import com.hp.it.perf.ac.core.service.AcServiceConfig;

class AcInternalDataBus implements AcDataListener<AcCommonDataWithPayLoad> {

	private static final Logger log = LoggerFactory
			.getLogger(AcInternalDataBus.class);

	private List<AcDataListener<AcCommonDataWithPayLoad>> downstreams = new ArrayList<AcDataListener<AcCommonDataWithPayLoad>>();

	@Inject
	private ContextDataDispatcher<AcCommonDataWithPayLoad> mainDispatcher;

	@Inject
	private AcServiceConfig serviceConfig;

	void registerDownstream(String name,
			AcDataListener<AcCommonDataWithPayLoad> downstream, int queueSize,
			int maxBufferSize, int threadCount) {
		downstreams.add(downstream);
		mainDispatcher
				.addDownstreamDispatcher(new ListenerDataDispatcher<AcCommonDataWithPayLoad>(
						name, queueSize, maxBufferSize, threadCount,
						downstream, AcCommonDataWithPayLoad.class));
	}

	@Override
	public void onData(final AcCommonDataWithPayLoad... data) {
		if (downstreams.isEmpty()) {
			log.warn("no downstream defined");
		}
		for (AcCommonDataWithPayLoad d : data) {
			validateData(d);
		}
		for (AcCommonDataWithPayLoad d : data) {
			try {
				mainDispatcher.add(d);
			} catch (InterruptedException e) {
				throw new AcCoreException("thread interrupted for on data", e);
			}
		}
	}

	protected void handleDownstreamError(
			AcDataListener<AcCommonDataWithPayLoad> downstream,
			Throwable throwable) {
		log.error("downstream data get error: " + downstream, throwable);
	}

	private void validateData(AcCommonDataWithPayLoad data) {
		// TODO error handling
		if (AcidHelper.isUnassigned(data.getAcid())) {
			return;
		}
	}

	public void onDataFinished() {
		try {
			mainDispatcher.closeDispatch();
		} catch (InterruptedException e) {
			throw new AcCoreException("thread interrupted for load finished", e);
		}
		AcStatusEvent event = new AcDataStatusEvent(
				serviceConfig.getCoreContext());
		serviceConfig.getCoreContext().getStatusBoard()
				.sendStatusEvent(Status.FINISHED.name(), event);
	}

}
