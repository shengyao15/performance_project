package com.hp.it.perf.ac.core.service;

import java.util.Set;

public interface AcServiceMetaData {

	// service id for data service (providing data repository function)
	String DATA_SERVICE_ID = "data";

	// service id for persistent service
	String PERSIST_SERVICE_ID = "persist";
	
	// service id for transform service
	String TRANSFORM_SERVICE_ID = "transform";
	
	// service id for dispatch service
	String DISPATCH_SERVICE_ID = "dispatch";
	
	// service id for transfer service
	String TRANSFER_SERVICE_ID = "transfer";

	public String getServiceId();

	public Set<String> getDependsServiceClassNames();

	public String getServiceClassName();

	public int getQueueSize();

	public int getMaxBufferSize();

}
