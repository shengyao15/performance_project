package com.hp.it.perf.ac.service.persist;

import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.service.AbstractServiceProvider;
import com.hp.it.perf.ac.core.service.AcServiceConfig;
import com.hp.it.perf.ac.core.service.AcServiceMetaData;
import com.hp.it.perf.ac.core.service.AcServiceProvider;
import com.hp.it.perf.ac.core.service.DefaultAcServiceMetaData;

public class AcPersistServiceProvider extends AbstractServiceProvider implements
		AcServiceProvider {

	@Override
	public AcServiceMetaData metadata() {
		DefaultAcServiceMetaData metadata = new DefaultAcServiceMetaData(
				"persist", AcPersistService.class);
		return metadata;
	}

	@Override
	public void init(AcServiceConfig serviceConfig) {
		// validateConfig(serviceConfig, "temporaryPath");
		// validateConfig(serviceConfig, "repositoryPath");
		// validateConfig(serviceConfig, "storeType");
		validateConfig(serviceConfig, "ac.persist.jdbc.driver");
		validateConfig(serviceConfig, "ac.persist.jdbc.url");
		validateConfig(serviceConfig, "ac.persist.jdbc.username");
		validateConfig(serviceConfig, "ac.persist.jdbc.password");
		validateConfig(serviceConfig, "ac.persist.hibernate.dialect");
		initClassPathApplicationContext(serviceConfig,
				"/spring/ac-service-persist.xml");
	}

	@Override
	public AcService getService() {
		return getApplicationContext().getBean(AcPersistService.class);
	}

}
