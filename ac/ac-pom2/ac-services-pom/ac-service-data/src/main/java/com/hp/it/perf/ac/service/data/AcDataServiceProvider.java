package com.hp.it.perf.ac.service.data;

import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.service.AbstractServiceProvider;
import com.hp.it.perf.ac.core.service.AcServiceConfig;
import com.hp.it.perf.ac.core.service.AcServiceMetaData;
import com.hp.it.perf.ac.core.service.AcServiceProvider;
import com.hp.it.perf.ac.core.service.DefaultAcServiceMetaData;

public class AcDataServiceProvider extends AbstractServiceProvider implements
		AcServiceProvider {

	@Override
	public AcServiceMetaData metadata() {
		DefaultAcServiceMetaData metadata = new DefaultAcServiceMetaData(
				AcServiceMetaData.DATA_SERVICE_ID, AcRepositoryService.class);
		return metadata;
	}

	@Override
	public void init(AcServiceConfig serviceConfig) {
		initClassPathApplicationContext(serviceConfig,
				"/spring/ac-service-data.xml");
	}

	@Override
	public AcService getService() {
		return getApplicationContext().getBean(AcRepositoryService.class);
	}

}
