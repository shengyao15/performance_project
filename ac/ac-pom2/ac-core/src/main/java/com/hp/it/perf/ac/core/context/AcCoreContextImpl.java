package com.hp.it.perf.ac.core.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.core.AcCoreContext;
import com.hp.it.perf.ac.core.AcCoreException;
import com.hp.it.perf.ac.core.AcCoreRuntime;
import com.hp.it.perf.ac.core.AcDataRepository;
import com.hp.it.perf.ac.core.AcPreferences;
import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.AcStatusBoard;
import com.hp.it.perf.ac.core.QueuedExecutor;
import com.hp.it.perf.ac.core.access.AcAccessException;
import com.hp.it.perf.ac.core.service.AcServiceException;
import com.hp.it.perf.ac.core.service.AcServiceManager;
import com.hp.it.perf.ac.core.service.AcServiceProvider;
import com.hp.it.perf.ac.core.service.intercept.AcServiceInterceptorManager;

@ManagedResource
class AcCoreContextImpl implements AcCoreContext {

	@Resource
	private ApplicationContext appContext;

	@Inject
	private AcSession session;

	@Inject
	private AcStatusBoard statusBoard;

	@Inject
	private AcDataRepository dataRepository;

	@Inject
	private AcServiceManager serviceManager;

	@Inject
	private AcCoreRuntime coreRuntime;

	@Inject
	private AcPreferences corePreferences;

	@Inject
	private AcServiceInterceptorManager serviceInterceptorManager;

	// service id <-> provider
	private Map<String, AcServiceProvider> providers = new HashMap<String, AcServiceProvider>();

	// service class name <-> service instance
	private Map<String, AcService> loadedServices = new HashMap<String, AcService>();

	private int defaultQueueSize = 10;

	private List<DefaultQueuedExecutor> executors = new ArrayList<DefaultQueuedExecutor>();

	private Map<String, AcCoreContextListener> contextListeners;

	public void registerServiceProvider(String serviceId,
			AcServiceProvider provider) {
		providers.put(serviceId, provider);
	}

	protected QueuedExecutor createQueuedExecutor(String name, int queueSize,
			int threadCount) {
		DefaultQueuedExecutor executor = new DefaultQueuedExecutor(name,
				queueSize, threadCount, new ThreadPoolExecutor.AbortPolicy());
		executors.add(executor);
		return executor;
	}

	@Override
	public AcStatusBoard getStatusBoard() {
		return statusBoard;
	}

	public void setDefaultQueueSize(int queueSize) {
		this.defaultQueueSize = queueSize;
	}

	public int getDefaultQueueSize() {
		return defaultQueueSize;
	}

	@Override
	public AcDataRepository getDataRepository() {
		return dataRepository;
	}

	@Override
	public QueuedExecutor createQueuedExecutor(String name) {
		// TODO properties file
		return createQueuedExecutor(name, defaultQueueSize, 1);
	}

	@Override
	public <T extends AcService> T getService(Class<T> serviceClass) {
		return serviceClass.cast(getServiceByClassName(serviceClass.getName()));
	}

	@Override
	public AcService getServiceByClassName(String serviceClassName)
			throws AcServiceException {
		AcService service = loadedServices.get(serviceClassName);
		if (service != null) {
			return service;
		}
		String serviceId = serviceManager
				.getServiceIdByClassName(serviceClassName);
		AcServiceProvider provider = providers.get(serviceId);
		if (provider == null) {
			throw new IllegalArgumentException(
					"service not defined in session: " + serviceId);
		}
		service = provider.getService();
		service = interceptService(serviceId, serviceClassName, provider,
				service);
		loadedServices.put(serviceClassName, service);
		return service;
	}

	private AcService interceptService(String serviceId,
			String serviceClassName, AcServiceProvider provider,
			AcService service) {
		Class<?> serviceInterface;
		try {
			serviceInterface = Class.forName(serviceClassName);
		} catch (ClassNotFoundException e) {
			throw new AcServiceException("cannot get service class name: "
					+ serviceClassName, e).setServiceId(serviceId);
		}
		if (!AcService.class.isAssignableFrom(serviceInterface)) {
			throw new AcServiceException(
					"service class is not sub class of AcService: "
							+ serviceInterface).setServiceId(serviceId);
		}
		if (!serviceInterface.isInterface()) {
			throw new AcServiceException("service class is not interface: "
					+ serviceInterface).setServiceId(serviceId);
		}
		InvocationHandler serviceProxy = serviceInterceptorManager
				.createServiceInvocationHandler(serviceId, service,
						serviceInterface.asSubclass(AcService.class));
		return (AcService) Proxy.newProxyInstance(service.getClass()
				.getClassLoader(), new Class<?>[] { serviceInterface },
				serviceProxy);
	}

	@PreDestroy
	void destroy() {
		for (AcServiceProvider provider : providers.values()) {
			// TODO destroy error
			provider.destroy();
		}
		providers.clear();
		// close executors
		for (DefaultQueuedExecutor executor : executors) {
			executor.shutdown();
		}
		for (DefaultQueuedExecutor executor : executors) {
			try {
				executor.shutdownAndAwait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public void close() {
		// deactive
		fireDeactive();
		// sync ac preferences
		try {
			corePreferences.sync();
		} catch (AcCoreException e) {
			// TODO sync error
			e.printStackTrace();
		}
		// close spring context
		if (appContext instanceof ConfigurableApplicationContext) {
			((ConfigurableApplicationContext) appContext).close();
		}
	}

	@Override
	public AcService getServiceById(String serviceId) throws AcServiceException {
		return getServiceByClassName(serviceManager
				.getServiceClassNameById(serviceId));
	}

	@ManagedAttribute
	@Override
	public String[] getLoadedServiceIds() {
		return providers.keySet().toArray(new String[providers.size()]);
	}

	@Override
	public AcCoreRuntime getCoreRuntime() {
		return coreRuntime;
	}

	private Map<String, AcCoreContextListener> getContextListeners() {
		if (contextListeners == null) {
			// to cache listener for destroy phase
			contextListeners = BeanFactoryUtils.beansOfTypeIncludingAncestors(
					appContext, AcCoreContextListener.class);
		}
		return contextListeners;
	}

	protected void fireActive() {
		Map<String, AcCoreContextListener> contextListeners = getContextListeners();
		for (AcCoreContextListener l : contextListeners.values()) {
			l.onCoreContextActive(this);
		}
	}

	protected void fireDeactive() {
		Map<String, AcCoreContextListener> contextListeners = getContextListeners();
		for (AcCoreContextListener l : contextListeners.values()) {
			l.onCoreContextDeactive(this);
		}
	}

	@Override
	public AcSession getSession() {
		return session;
	}

	@Override
	public AcPreferences getCorePreferences() {
		return corePreferences;
	}

	@Override
	public String getServiceClassNameById(String serviceId)
			throws AcAccessException, AcServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
