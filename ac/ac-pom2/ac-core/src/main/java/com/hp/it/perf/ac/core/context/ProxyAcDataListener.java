package com.hp.it.perf.ac.core.context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.hp.it.perf.ac.common.core.AcDataListener;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.core.AcCoreException;

class ProxyAcDataListener implements AcDataListener<AcCommonDataWithPayLoad> {
	private final Method method;
	private final Object proxy;

	public ProxyAcDataListener(Object obj, Method method) {
		this.proxy = obj;
		this.method = method;
	}

	@Override
	public void onData(AcCommonDataWithPayLoad... data) {
		try {
			method.invoke(proxy, new Object[] { data });
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
			throw new AcCoreException("process data dispatch error: "
					+ e.getCause(), e.getCause());
		}
	}

}
