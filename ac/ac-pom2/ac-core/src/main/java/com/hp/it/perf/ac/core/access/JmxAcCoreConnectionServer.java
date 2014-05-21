package com.hp.it.perf.ac.core.access;

import javax.inject.Inject;

import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.core.AcCoreContext;
import com.hp.it.perf.ac.core.AcDataRepository;
import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.service.AcServiceException;
import com.hp.it.perf.ac.core.service.AcServiceManager;

public class JmxAcCoreConnectionServer implements AcCoreAccess {

	@Inject
	private AcCoreContext coreContext;

	@Inject
	private AcServiceManager serviceManager;

	// use specified class
	@Inject
	private JmxAcDataRepository dataRepository;

	// Not managed via JMX
	@Override
	public AcDataRepository getDataRepository() {
		return dataRepository;
	}

	// Not managed via JMX
	@Override
	public <T extends AcService> T getService(Class<T> serviceClass)
			throws AcServiceException {
		return coreContext.getService(serviceClass);
	}

	@Override
	public String getServiceClassNameById(String serviceId)
			throws AcServiceException {
		return serviceManager.getServiceClassNameById(serviceId);
	}

	@Override
	public String[] getLoadedServiceIds() {
		return coreContext.getLoadedServiceIds();
	}

	@Override
	public AcSession getSession() {
		return coreContext.getSession();
	}

}
