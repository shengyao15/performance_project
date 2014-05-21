package com.hp.it.perf.ac.core.service.intercept;

import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.context.ApplicationContext;

import com.hp.it.perf.ac.core.AcCoreRuntime;
import com.hp.it.perf.ac.core.AcService;

public class AcServiceInterceptorManager {

	@Inject
	private AcCoreRuntime coreRuntime;

	@Resource
	private ApplicationContext context;

	private volatile int modCount = 0;

	private List<AcServiceInterceptor> interceptors = new ArrayList<AcServiceInterceptor>();

	private Map<Class<?>, AcServiceInterceptor> singletonInterceptors = new HashMap<Class<?>, AcServiceInterceptor>();

	AcCoreRuntime getCoreRuntime() {
		return coreRuntime;
	}

	AcServiceInterceptor[] getInterceptors(String serviceId) {
		return interceptors.toArray(new AcServiceInterceptor[0]);
	}

	public void registerServiceInterceptor(AcServiceInterceptor interceptor) {
		if (interceptors.add(interceptor)) {
			modCount++;
		}
	}

	public void unRegisterServiceInterceptor(AcServiceInterceptor interceptor) {
		if (interceptors.remove(interceptor)) {
			modCount++;
		}
	}

	public InvocationHandler createServiceInvocationHandler(String serviceId,
			AcService service, Class<? extends AcService> serviceInf) {
		return new AcServiceInterceptProxy(this, serviceId, service, serviceInf);
	}

	int getModCount() {
		return modCount;
	}

	AcServiceInterceptor createSingletonInterceptor(
			Class<? extends AcServiceInterceptor> interceptorClass) {
		AcServiceInterceptor interceptor = singletonInterceptors
				.get(interceptorClass);
		if (interceptor == null) {
			String[] beanNames = context.getBeanNamesForType(interceptorClass);
			if (beanNames.length == 0) {
				// create by default constructor
				try {
					interceptor = interceptorClass.newInstance();
				} catch (Exception e) {
					throw new AcServiceInterceptException(e);
				}
			} else {
				interceptor = context.getBean(interceptorClass);
			}
			singletonInterceptors.put(interceptorClass, interceptor);
		}
		return interceptor;
	}

}
