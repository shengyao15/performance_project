package com.hp.it.perf.ac.service.spfchain;

import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.service.AbstractServiceProvider;
import com.hp.it.perf.ac.core.service.AcServiceConfig;
import com.hp.it.perf.ac.core.service.AcServiceMetaData;
import com.hp.it.perf.ac.core.service.AcServiceProvider;
import com.hp.it.perf.ac.core.service.DefaultAcServiceMetaData;
import com.hp.it.perf.ac.service.chain.ChainService;

public class SpfChainServiceProvider extends AbstractServiceProvider implements
		AcServiceProvider {

	private DefaultAcServiceMetaData metadata;

	public SpfChainServiceProvider() {
		metadata = new DefaultAcServiceMetaData("spfchain",
				SpfChainService.class);
		metadata.addDependsServiceClassName(ChainService.class);
	}

	@Override
	public void init(AcServiceConfig serviceConfig) {
		initClassPathApplicationContext(serviceConfig,
				"/spring/spf-service-chain.xml");
	}

	@Override
	public AcServiceMetaData metadata() {
		return metadata;
	}

	@Override
	public AcService getService() {
		return getApplicationContext().getBean(SpfChainService.class);
	}

}
