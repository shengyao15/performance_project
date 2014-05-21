package com.hp.it.perf.ac.core.context;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.common.core.AcDataListener;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.core.AcDataSubscriber;
import com.hp.it.perf.ac.core.AcDispatchInfo;
import com.hp.it.perf.ac.core.AcDispatchRegistry;

class AcDispatchRegistryImpl implements AcDispatchRegistry {

	private static final Logger log = LoggerFactory
			.getLogger(AcDispatchRegistryImpl.class);
	private Map<String, AcDataListener<AcCommonDataWithPayLoad>> downstreams = new HashMap<String, AcDataListener<AcCommonDataWithPayLoad>>();
	private Map<String, AcDispatchInfo> infos = new HashMap<String, AcDispatchInfo>();

	@Override
	public void register(String name,
			AcDataListener<AcCommonDataWithPayLoad> downstream,
			AcDispatchInfo info) {
		downstreams.put(name, downstream);
		infos.put(name, info);
	}

	@Override
	public void unregister(String name) {
		downstreams.remove(name);
		infos.remove(name);
	}

	@Override
	public String[] getNames() {
		return downstreams.keySet().toArray(new String[downstreams.size()]);
	}

	@Override
	public AcDataListener<AcCommonDataWithPayLoad> getDataListener(String name) {
		return downstreams.get(name);
	}

	@Override
	public AcDispatchInfo getDispatchInfo(String name) {
		return infos.get(name);
	}

	@Override
	public void processRegister(Object obj, String defaultName) {
		if (obj == null) {
			return;
		}
		Method[] methods = obj.getClass().getMethods();
		if (log.isDebugEnabled()) {
			log.debug("Looking for DataSubscriber annotations for class {}",
					obj.getClass());
		}
		for (Method method : methods) {
			AcDataSubscriber subscriberAnnotation = method
					.getAnnotation(AcDataSubscriber.class);
			if (subscriberAnnotation == null) {
				continue;
			}
			log.debug("Found AcDataSubscriber: {} on method {}",
					subscriberAnnotation, method);

			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length != 1) {
				throw new IllegalArgumentException(
						"incorrect subscriber method, expect 1 argument: "
								+ method);
			}
			Class<?> paramDataType = parameterTypes[0];
			if (paramDataType.isArray()) {
				paramDataType = paramDataType.getComponentType();
			}
			if (!paramDataType
					.isAssignableFrom(subscriberAnnotation.dataType())) {
				throw new IllegalArgumentException(
						"incorrect subscriber argument, expect (array of) class/supper-class of "
								+ subscriberAnnotation.dataType() + ": "
								+ paramDataType);
			}
			// TODO hard code to check data type with AcCommonDataWithPayLoad
			if (!subscriberAnnotation.dataType().isAssignableFrom(
					AcCommonDataWithPayLoad.class)) {
				throw new IllegalArgumentException(
						"incorrect data type of subscriber, expect (super) "
								+ AcCommonDataWithPayLoad.class + ": "
								+ subscriberAnnotation.dataType());
			}

			// Check args
			AcDispatchInfo info = new AcDispatchInfo();
			info.setMaxBufferSize(subscriberAnnotation.maxBufferSize());
			info.setQueueSize(subscriberAnnotation.queueSize());
			info.setThreadCount(subscriberAnnotation.threadCount());
			info.setMaxWaitTime(subscriberAnnotation.maxWaitTime());
			String name = subscriberAnnotation.value();
			if (name == null || name.length() == 0) {
				name = defaultName;
			}
			// make sure access method ok
			method.setAccessible(true);
			AcDataListener<AcCommonDataWithPayLoad> downstream = new ProxyAcDataListener(
					obj, method);
			register(name, downstream, info);
			log.debug("subscribe to data listener from method: {} ", method);
		}
	}

}
