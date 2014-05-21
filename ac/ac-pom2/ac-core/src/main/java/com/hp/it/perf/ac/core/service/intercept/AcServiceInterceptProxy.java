package com.hp.it.perf.ac.core.service.intercept;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.it.perf.ac.core.AcService;

class AcServiceInterceptProxy implements InvocationHandler {

	private String serviceId;
	private AcService service;
	private AcServiceInterceptorManager manager;
	private Class<?> serviceClass;
	private Map<Method, List<Annotation>> methodAnnotationList = new HashMap<Method, List<Annotation>>();
	private Map<Method, InterceptorAnnotationPair[]> methodInterceptorList = new HashMap<Method, InterceptorAnnotationPair[]>();
	private int managerModCount;

	static class InterceptorAnnotationPair {
		final AcServiceInterceptor interceptor;
		final Annotation annotation;

		private InterceptorAnnotationPair(AcServiceInterceptor interceptor,
				Annotation annotation) {
			this.interceptor = interceptor;
			this.annotation = annotation;
		}
	}

	public AcServiceInterceptProxy(AcServiceInterceptorManager manager,
			String serviceId, AcService service, Class<?> serviceClass) {
		this.manager = manager;
		this.serviceId = serviceId;
		this.service = service;
		this.serviceClass = serviceClass;
		inspect();
	}

	private void inspect() {
		managerModCount = manager.getModCount();
		// first global, then type level, then method level
		// but later one can override pre (for same annotation)
		List<Annotation> typeLevel = new ArrayList<Annotation>();
		Class<?> serviceType = serviceClass;
		while (serviceType != null) {
			for (Annotation ann : serviceType.getAnnotations()) {
				if (ann.annotationType().isAnnotationPresent(
						AcServiceIntercepted.class)) {
					putOrAppendAnnotation(typeLevel, ann);
				}
			}
			Class<?> theType = serviceType;
			serviceType = null;
			for (Class<?> superInf : theType.getInterfaces()) {
				if (AcService.class.isAssignableFrom(superInf)) {
					serviceType = superInf;
					break;
				}
			}
		}
		for (Method method : serviceClass.getMethods()) {
			List<Annotation> annList = new ArrayList<Annotation>(typeLevel);
			for (Annotation ann : method.getAnnotations()) {
				if (ann.annotationType().isAnnotationPresent(
						AcServiceIntercepted.class)) {
					putOrAppendAnnotation(annList, ann);
				}
			}
			methodAnnotationList.put(method, annList);
			setupRuntimeInterceptorList(method);
		}
	}

	private void setupRuntimeInterceptorList(Method method) {
		List<InterceptorAnnotationPair> runtimeInterceptorList = new ArrayList<InterceptorAnnotationPair>();
		for (AcServiceInterceptor interceptor : manager
				.getInterceptors(serviceId)) {
			runtimeInterceptorList.add(new InterceptorAnnotationPair(
					interceptor, null));
		}
		List<Annotation> annList = methodAnnotationList.get(method);
		for (Annotation ann : annList) {
			runtimeInterceptorList
					.add(new InterceptorAnnotationPair(manager
							.createSingletonInterceptor(ann.annotationType()
									.getAnnotation(AcServiceIntercepted.class)
									.value()), ann));
		}
		methodInterceptorList.put(method, runtimeInterceptorList
				.toArray(new InterceptorAnnotationPair[runtimeInterceptorList
						.size()]));
	}

	private void putOrAppendAnnotation(List<Annotation> list, Annotation ann) {
		for (int i = 0; i < list.size(); i++) {
			Annotation theAnn = list.get(i);
			if (theAnn.annotationType().equals(ann.annotationType())) {
				// replace it
				list.set(i, ann);
				return;
			}
		}
		list.add(ann);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (manager.getModCount() != managerModCount) {
			synchronized (this) {
				if (manager.getModCount() != managerModCount) {
					int modCount = manager.getModCount();
					setupRuntimeInterceptorList(method);
					managerModCount = modCount;
				}
			}
		}
		InterceptorAnnotationPair[] interceptorAnnPair = methodInterceptorList
				.get(method);
		AcServiceInvokeContext context = new AcServiceInvokeContext(
				interceptorAnnPair);
		context.setArguments(args);
		context.setMethod(method);
		context.setService(service);
		context.setCoreRuntime(manager.getCoreRuntime());
		try {
			return context.proceed();
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

}
