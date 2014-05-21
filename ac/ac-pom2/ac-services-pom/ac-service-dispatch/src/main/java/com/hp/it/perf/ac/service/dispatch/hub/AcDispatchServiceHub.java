package com.hp.it.perf.ac.service.dispatch.hub;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.common.core.AcDataHubEndpoint;
import com.hp.it.perf.ac.common.core.AcDataListener;
import com.hp.it.perf.ac.common.core.AcStatusEvent;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.common.model.AcidHelper;
import com.hp.it.perf.ac.core.AcDataStatusEvent;
import com.hp.it.perf.ac.core.AcDispatchInfo;
import com.hp.it.perf.ac.core.AcDispatchRegistry;
import com.hp.it.perf.ac.core.AcStatusSubscriber;
import com.hp.it.perf.ac.core.AcStatusSubscriber.Status;
import com.hp.it.perf.ac.core.hub.SimpleAcDataHubImpl;
import com.hp.it.perf.ac.core.service.AcServiceConfig;
import com.hp.it.perf.ac.service.dispatch.AcDispatchService;

public class AcDispatchServiceHub implements AcDispatchService {

	private static final Logger log = LoggerFactory
			.getLogger(AcDispatchServiceHub.class);

	private SimpleAcDataHubImpl<AcCommonDataWithPayLoad> hub = null;

	private Map<String, AcDataHubEndpoint<AcCommonDataWithPayLoad>> downstreams = new HashMap<String, AcDataHubEndpoint<AcCommonDataWithPayLoad>>();

	@Inject
	private AcServiceConfig serviceConfig;

	private long lastLogTime;

	public AcDispatchServiceHub(final int capacity) {
		hub = new SimpleAcDataHubImpl<AcCommonDataWithPayLoad>(
				AcCommonDataWithPayLoad.class, capacity,
				Executors.newCachedThreadPool());
	}

	@Override
	public void dispatch(AcCommonDataWithPayLoad... data) {
		for (AcCommonDataWithPayLoad d : data) {
			validateData(d);
		}
		hub.onData(data);
		// log current downstream status per minute
		long now = System.currentTimeMillis();
		if (lastLogTime < now - TimeUnit.MINUTES.toMillis(1)) {
			lastLogTime = now;
			StringBuilder msg = new StringBuilder();
			for (AcDataHubEndpoint<AcCommonDataWithPayLoad> endpoint : downstreams
					.values()) {
				msg.append(String
						.format("[%s] (total-processed: %s, total-lost: %s, in-pending: %s)",
								endpoint.getName(), endpoint.getProcessed(),
								endpoint.getTotalLosted(),
								endpoint.getUnprocessed()));
			}
			log.info("Process status: {}", msg);
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

	@Override
	public void closeDispatch() {
		try {
			hub.shutdown(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		AcStatusEvent event = new AcDataStatusEvent(
				serviceConfig.getCoreContext());
		serviceConfig.getCoreContext().getStatusBoard()
				.sendStatusEvent(Status.FINISHED.name(), event);
	}

	@Override
	public void registerDownstream(String name,
			AcDataListener<AcCommonDataWithPayLoad> downstream,
			AcDispatchInfo info) {
		AcDispatchHubHandler listener = new AcDispatchHubHandler(name,
				downstream, info);
		AcDataHubEndpoint<AcCommonDataWithPayLoad> endpoint = hub
				.createDataEndpoint(listener, info.getMaxBufferSize(),
						info.getMaxWaitTime());
		endpoint.setName(name);
		listener.endpoint = endpoint;
		downstreams.put(name, endpoint);
	}

	@Override
	public void unregisterDownstream(String name) {
		AcDataHubEndpoint<AcCommonDataWithPayLoad> endpoint = downstreams
				.remove(name);
		if (endpoint != null) {
			endpoint.close();
		}
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
