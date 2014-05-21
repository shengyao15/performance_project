package com.hp.it.perf.ac.service.transform;

import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.service.AbstractServiceProvider;
import com.hp.it.perf.ac.core.service.AcServiceConfig;
import com.hp.it.perf.ac.core.service.AcServiceMetaData;
import com.hp.it.perf.ac.core.service.AcServiceProvider;
import com.hp.it.perf.ac.core.service.DefaultAcServiceMetaData;
import com.hp.it.perf.ac.service.dispatch.AcDispatchService;

public class AcTransformServiceProvider extends AbstractServiceProvider
		implements AcServiceProvider {

	@Override
	public AcServiceMetaData metadata() {
		DefaultAcServiceMetaData metadata = new DefaultAcServiceMetaData(
				AcServiceMetaData.TRANSFORM_SERVICE_ID,
				AcTransformService.class);
		metadata.addDependsServiceClassName(AcDispatchService.class);
		return metadata;
	}

	@Override
	public void init(AcServiceConfig serviceConfig) {
		initXmlApplicationContext(serviceConfig, true);
	}

	@Override
	public AcService getService() {
		return getApplicationContext().getBean(AcTransformService.class);
	}

}
