package com.hp.it.perf.ac.core;

import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.core.access.AcCoreAccess;
import com.hp.it.perf.ac.core.service.AcServiceException;

public interface AcCoreContext extends AcCoreAccess{

	public AcSession getSession();

	public AcStatusBoard getStatusBoard();

	public AcDataRepository getDataRepository();

	public QueuedExecutor createQueuedExecutor(String name);

	public <T extends AcService> T getService(Class<T> serviceClass)
			throws AcServiceException;

	public AcService getServiceByClassName(String serviceClassName)
			throws AcServiceException;

	public AcService getServiceById(String serviceId) throws AcServiceException;

	public String[] getLoadedServiceIds();

	public AcCoreRuntime getCoreRuntime();
	
	public AcPreferences getCorePreferences();

	public void close();

}
