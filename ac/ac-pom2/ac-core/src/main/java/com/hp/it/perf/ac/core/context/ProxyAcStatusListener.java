package com.hp.it.perf.ac.core.context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;

import com.hp.it.perf.ac.common.core.AcStatusEvent;
import com.hp.it.perf.ac.common.core.AcStatusListener;
import com.hp.it.perf.ac.core.AcCoreException;
import com.hp.it.perf.ac.core.AcStatusSubscriber.Status;

class ProxyAcStatusListener implements AcStatusListener {
	private final EnumSet<Status> statusSet;
	private final Method method;
	private final Object proxy;

	public ProxyAcStatusListener(Object obj, Method method, Status[] status) {
		this.proxy = obj;
		this.method = method;
		this.statusSet = status.length == 0 ? EnumSet.noneOf(Status.class)
				: EnumSet.copyOf(Arrays.asList(status));
	}

	protected void onStatusEvent(Status status, AcStatusEvent event) {
		if (!statusSet.contains(status)) {
			return;
		}
		try {
			if (method.getParameterTypes().length == 0) {
				method.invoke(proxy);
			} else {
				method.invoke(proxy, event);
			}
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(method.toString(), e);
		} catch (InvocationTargetException e) {
			throw new AcCoreException("process status event error: "
					+ e.getCause(), e.getCause());
		}
	}

	@Override
	public void onActive(AcStatusEvent event) {
		onStatusEvent(Status.ACTIVE, event);
	}

	@Override
	public void onDeactive(AcStatusEvent event) {
		onStatusEvent(Status.DEACTIVE, event);
	}

	@Override
	public void onStatus(String status, AcStatusEvent event) {
		onStatusEvent(Status.valueOf(status), event);
	}

}
