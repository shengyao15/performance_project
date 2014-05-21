package com.hp.it.perf.ac.core.access;

import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.core.AcDataRepository;
import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.service.AcServiceException;

public interface AcCoreAccess {

	public AcSession getSession();

	public AcDataRepository getDataRepository() throws AcAccessException;

	public <T extends AcService> T getService(Class<T> serviceClass)
			throws AcAccessException, AcServiceException;

	public String getServiceClassNameById(String serviceId)
			throws AcAccessException, AcServiceException;

	public String[] getLoadedServiceIds() throws AcAccessException;

}
