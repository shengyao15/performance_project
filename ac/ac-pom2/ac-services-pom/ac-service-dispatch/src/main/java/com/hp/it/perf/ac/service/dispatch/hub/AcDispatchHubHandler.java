package com.hp.it.perf.ac.service.dispatch.hub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.common.core.AcDataHubEndpoint;
import com.hp.it.perf.ac.common.core.AcDataHubEndpoint.AcDataLostEvent;
import com.hp.it.perf.ac.common.core.AcDataListener;
import com.hp.it.perf.ac.common.core.AcStatusEvent;
import com.hp.it.perf.ac.common.core.AcStatusListener;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.core.AcDispatchInfo;
import com.hp.it.perf.ac.core.context.ListenerDataDispatcher;

public class AcDispatchHubHandler implements
		AcDataListener<AcCommonDataWithPayLoad>, AcStatusListener {

	private static final Logger log = LoggerFactory
			.getLogger(AcDispatchHubHandler.class);

	private String name;

	private ListenerDataDispatcher<AcCommonDataWithPayLoad> downstream;

	private AcDataListener<AcCommonDataWithPayLoad> directDownstream;

	volatile AcDataHubEndpoint<?> endpoint;

	public AcDispatchHubHandler(String name,
			AcDataListener<AcCommonDataWithPayLoad> downstream,
			AcDispatchInfo info) {
		this.name = name;
		if (info.getThreadCount() == 1) {
			directDownstream = downstream;
		} else {
			this.downstream = new ListenerDataDispatcher<AcCommonDataWithPayLoad>(
					name, info.getQueueSize(), info.getMaxBufferSize(),
					info.getThreadCount(), downstream,
					AcCommonDataWithPayLoad.class);
		}
	}

	@Override
	public void onData(AcCommonDataWithPayLoad... data) {
		log.debug("Process in [{}]: {} (pending:{}, total-lost:{})",
				new Object[] { name, data.length, endpoint.getUnprocessed(),
						endpoint.getTotalLosted() });
		if (directDownstream != null) {
			directDownstream.onData(data);
		} else {
			for (AcCommonDataWithPayLoad e : data) {
				try {
					downstream.add(e);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					return;
				}
			}
		}
	}

	@Override
	public void onActive(AcStatusEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDeactive(AcStatusEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatus(String status, AcStatusEvent event) {
		if (event instanceof AcDataLostEvent) {
			log.warn("data LOST on hub endpoint {}: {} (total: {})", name,
					((AcDataLostEvent) event).getLost(),
					endpoint.getTotalLosted());
		}
	}

}
