package com.hp.it.perf.ac.core.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.common.core.AcStatusEvent;
import com.hp.it.perf.ac.common.core.AcStatusListener;
import com.hp.it.perf.ac.core.AcStatusListenerRegistry;
import com.hp.it.perf.ac.core.AcStatusSubscriber;
import com.hp.it.perf.ac.core.AcStatusSubscriber.Status;

class AcStatusListenerRegistryImpl implements AcStatusListenerRegistry,
		AcStatusListener {

	private static final Logger log = LoggerFactory
			.getLogger(AcStatusListenerRegistryImpl.class);

	private List<AcStatusListener> listeners = new ArrayList<AcStatusListener>();

	@Override
	public void attachStatusListener(AcStatusListener listener) {
		listeners.add(listener);
	}

	@Override
	public void onActive(AcStatusEvent event) {
		for (AcStatusListener al : listeners) {
			al.onActive(event);
		}
	}

	@Override
	public void onDeactive(AcStatusEvent event) {
		for (AcStatusListener al : listeners) {
			al.onDeactive(event);
		}
	}

	@Override
	public void processStatusListener(Object obj) {
		if (obj == null) {
			return;
		}
		// get public methods
		Method[] methods = obj.getClass().getMethods();
		if (log.isDebugEnabled()) {
			log.debug("Looking for StatusSubscriber annotations for class {}",
					obj.getClass());
		}
		for (Method method : methods) {
			AcStatusSubscriber subscriberAnnotation = method
					.getAnnotation(AcStatusSubscriber.class);
			if (subscriberAnnotation == null) {
				continue;
			}
			log.debug("Found AcStatusSubscriber: {} on method {}",
					subscriberAnnotation, method);

			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length > 1) {
				throw new IllegalArgumentException(
						"incorrect subscriber method, expect less or equal 1 argument: "
								+ method);
			}
			if (parameterTypes.length == 1
					&& !AcStatusEvent.class.isAssignableFrom(parameterTypes[0])) {
				throw new IllegalArgumentException(
						"incorrect subscriber argument, expect class/sub-class of "
								+ AcStatusEvent.class + ": "
								+ parameterTypes[0]);
			}

			Status[] status = subscriberAnnotation.value();
			if (status.length == 0) {
				throw new IllegalArgumentException(
						"incorrect status on subscriber: " + method);
			}
			// make sure access the method (if class is package private)
			// and improve reflection performance 
			method.setAccessible(true);
			AcStatusListener proxy = new ProxyAcStatusListener(obj, method,
					status);
			attachStatusListener(proxy);
			log.debug("subscribe to status listener from method: {} ", method);
		}
	}

	@Override
	public void onStatus(String status, AcStatusEvent event) {
		for (AcStatusListener al : listeners) {
			al.onStatus(status, event);
		}
	}

	@Override
	public AcStatusListener[] getStatusListeners() {
		return listeners.toArray(new AcStatusListener[listeners.size()]);
	}
}
