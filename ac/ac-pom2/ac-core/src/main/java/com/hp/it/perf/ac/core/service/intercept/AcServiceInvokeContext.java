package com.hp.it.perf.ac.core.service.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import com.hp.it.perf.ac.core.AcCoreRuntime;
import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.service.intercept.AcServiceInterceptProxy.InterceptorAnnotationPair;

public class AcServiceInvokeContext {

	private Method method;

	private Object[] arguments;

	private AcService service;

	private AcCoreRuntime coreRuntime;

	private InterceptorAnnotationPair[] interceptorAnnPair;

	private int index;

	private String contextId = UUID.randomUUID().toString();

	private Annotation interceptedAnnotation;

	void setMethod(Method method) {
		this.method = method;
	}

	void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	void setService(AcService service) {
		this.service = service;
	}

	void setCoreRuntime(AcCoreRuntime coreRuntime) {
		this.coreRuntime = coreRuntime;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public AcService getService() {
		return service;
	}

	public AcCoreRuntime getCoreRuntime() {
		return coreRuntime;
	}

	AcServiceInvokeContext(InterceptorAnnotationPair[] interceptorAnnPair) {
		this.interceptorAnnPair = interceptorAnnPair;
		this.index = 0;
	}

	public <T extends Annotation> T getInterceptAnnotation(
			Class<T> annotationType) {
		if (interceptedAnnotation.annotationType() == annotationType) {
			return annotationType.cast(interceptedAnnotation);
		} else {
			return null;
		}
	}

	public Object proceed() throws InvocationTargetException {
		AcServiceInterceptor interceptor = null;
		Annotation prevAnnotation = interceptedAnnotation;
		if (index < interceptorAnnPair.length) {
			InterceptorAnnotationPair pair = interceptorAnnPair[index++];
			interceptor = pair.interceptor;
			interceptedAnnotation = pair.annotation;
		}
		if (interceptor != null) {
			try {
				try {
					return interceptor.invokeService(this);
				} catch (InvocationTargetException e) {
					throw e;
				} catch (Throwable t) {
					throw new AcServiceInterceptException(t);
				}
			} finally {
				index--;
				interceptedAnnotation = prevAnnotation;
			}
		} else {
			interceptedAnnotation = prevAnnotation;
			try {
				return method.invoke(service, arguments);
			} catch (IllegalAccessException e) {
				throw new AcServiceInterceptException(e);
			} catch (IllegalArgumentException e) {
				throw new AcServiceInterceptException(e);
			}
		}
	}

}
