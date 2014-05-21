package com.hp.it.perf.ac.core.service;

import java.util.HashSet;
import java.util.Set;

import com.hp.it.perf.ac.core.AcService;

public class DefaultAcServiceMetaData implements AcServiceMetaData {

	private String serviceId;

	private String serviceClassName;

	private Set<String> dependsServices = new HashSet<String>();

	private int queueSize = 1000;

	private int bufferSize = queueSize;

	public DefaultAcServiceMetaData(String serviceId, String serviceClassName) {
		this.serviceId = serviceId;
		this.serviceClassName = serviceClassName;
	}

	public DefaultAcServiceMetaData(String serviceId,
			Class<? extends AcService> serviceClass) {
		this(serviceId, serviceClass.getName());
	}

	@Override
	public String getServiceId() {
		return serviceId;
	}

	@Override
	public Set<String> getDependsServiceClassNames() {
		return dependsServices;
	}

	@Override
	public String getServiceClassName() {
		return serviceClassName;
	}

	public <T extends AcService> DefaultAcServiceMetaData addDependsServiceClassName(
			Class<T> dependServiceClass) {
		dependsServices.add(dependServiceClass.getName());
		return this;
	}

	public void setQueueSize(int size) {
		if (size <= 0)
			size = 0;
		queueSize = size;
	}

	@Override
	public int getQueueSize() {
		return queueSize;
	}

	@Override
	public int getMaxBufferSize() {
		return bufferSize;
	}
}
