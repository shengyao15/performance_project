package com.hp.it.perf.ac.client.load;

import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.service.AbstractServiceProvider;
import com.hp.it.perf.ac.core.service.AcServiceConfig;
import com.hp.it.perf.ac.core.service.AcServiceMetaData;
import com.hp.it.perf.ac.core.service.AcServiceProvider;
import com.hp.it.perf.ac.core.service.DefaultAcServiceMetaData;
import com.hp.it.perf.ac.service.transfer.AcTransferService;

public class AcRealtimeLoadServiceProvider extends AbstractServiceProvider
		implements AcServiceProvider {

	private DefaultAcServiceMetaData metadata;

	public AcRealtimeLoadServiceProvider() {
		metadata = new DefaultAcServiceMetaData("loadclient",
				AcRealtimeLoadService.class);
		metadata.addDependsServiceClassName(AcTransferService.class);
	}

	@Override
	public void init(AcServiceConfig serviceConfig) {
		initXmlApplicationContext(serviceConfig, true);
	}

	@Override
	public AcServiceMetaData metadata() {
		return metadata;
	}

	@Override
	public AcService getService() {
		return getApplicationContext().getBean(AcRealtimeLoadService.class);
	}

}
