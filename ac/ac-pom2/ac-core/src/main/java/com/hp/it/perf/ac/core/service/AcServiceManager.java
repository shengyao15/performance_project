package com.hp.it.perf.ac.core.service;

import java.util.List;


public interface AcServiceManager {
	public String getProviderClassName(String serviceId)
			throws AcServiceException;

	public String[] getServiceIdList();

	public AcServiceProvider createProvider(String serviceId)
			throws AcServiceException;

	public String getServiceIdByClassName(String serviceClassName)
			throws AcServiceException;

	public String getServiceClassNameById(String serviceId)
			throws AcServiceException;

	public List<String> sortService(List<String> serviceIds);

}
