package com.hp.it.perf.ac.service.chain;

import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.service.AbstractServiceProvider;
import com.hp.it.perf.ac.core.service.AcServiceConfig;
import com.hp.it.perf.ac.core.service.AcServiceMetaData;
import com.hp.it.perf.ac.core.service.AcServiceProvider;
import com.hp.it.perf.ac.core.service.DefaultAcServiceMetaData;

public class ChainServiceProvider extends AbstractServiceProvider implements
		AcServiceProvider {

	private AcServiceMetaData metadata = new DefaultAcServiceMetaData("chain",
			ChainService.class);

	@Override
	public void init(AcServiceConfig serviceConfig) {
		validateConfig(serviceConfig, "graphContext.storeDirectory");
		initClassPathApplicationContext(serviceConfig,
				"/spring/ac-service-chain.xml");
	}

	@Override
	public AcServiceMetaData metadata() {
		return metadata;
	}

	@Override
	public AcService getService() {
		return getApplicationContext().getBean(ChainService.class);
	}

}
