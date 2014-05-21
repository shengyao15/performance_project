package com.hp.it.perf.ac.app.hpsc.search.service;

import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.service.AbstractServiceProvider;
import com.hp.it.perf.ac.core.service.AcServiceConfig;
import com.hp.it.perf.ac.core.service.AcServiceMetaData;
import com.hp.it.perf.ac.core.service.AcServiceProvider;
import com.hp.it.perf.ac.core.service.DefaultAcServiceMetaData;
import com.hp.it.perf.ac.service.data.AcRepositoryService;

public class HpscQueryServiceProvider extends AbstractServiceProvider
		implements AcServiceProvider {

	@Override
	public AcServiceMetaData metadata() {
		DefaultAcServiceMetaData metadata = new DefaultAcServiceMetaData(
				"hpscSearch", HpscQueryService.class);
		metadata.addDependsServiceClassName(AcRepositoryService.class);
		return metadata;
	}

	@Override
	public void init(AcServiceConfig serviceConfig) {
		validateConfig(serviceConfig, "ac.persist.jdbc.driver");
		validateConfig(serviceConfig, "ac.persist.jdbc.url");
		validateConfig(serviceConfig, "ac.persist.jdbc.username");
		validateConfig(serviceConfig, "ac.persist.jdbc.password");
		initClassPathApplicationContext(serviceConfig,
				"/spring/hpsc-search-service.xml");
	}

	@Override
	public AcService getService() {
		return getApplicationContext().getBean(HpscQueryService.class);
	}

}
