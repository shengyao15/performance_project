package com.hp.it.perf.ac.service.dispatch;

import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.service.AbstractServiceProvider;
import com.hp.it.perf.ac.core.service.AcServiceConfig;
import com.hp.it.perf.ac.core.service.AcServiceMetaData;
import com.hp.it.perf.ac.core.service.AcServiceProvider;
import com.hp.it.perf.ac.core.service.DefaultAcServiceMetaData;

public class AcDispatchServiceProvider extends AbstractServiceProvider
		implements AcServiceProvider {

	@Override
	public AcServiceMetaData metadata() {
		DefaultAcServiceMetaData metadata = new DefaultAcServiceMetaData(
				AcServiceMetaData.DISPATCH_SERVICE_ID,
				AcDispatchService.class);
		return metadata;
	}

	@Override
	public void init(AcServiceConfig serviceConfig) {
		initClassPathApplicationContext(serviceConfig, "/spring/ac-service-dispatch.xml");
	}

	@Override
	public AcService getService() {
		return getApplicationContext().getBean(AcDispatchService.class);
	}

}
