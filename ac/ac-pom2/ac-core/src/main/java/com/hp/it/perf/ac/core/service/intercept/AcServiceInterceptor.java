package com.hp.it.perf.ac.core.service.intercept;

import java.lang.reflect.InvocationTargetException;

public interface AcServiceInterceptor {

	public Object invokeService(AcServiceInvokeContext invokeContext)
			throws InvocationTargetException, Throwable;

}
