package com.hp.it.perf.ac.core.service;

import com.hp.it.perf.ac.core.AcService;

public interface AcServiceProvider {

	// Metadata
	public AcServiceMetaData metadata();

	// Service Life-cycle
	public void init(AcServiceConfig serviceConfig);

	public void destroy();

	public AcService getService();
}
