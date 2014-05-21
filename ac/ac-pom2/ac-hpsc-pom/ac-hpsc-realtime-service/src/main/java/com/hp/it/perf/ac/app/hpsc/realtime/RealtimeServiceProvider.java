package com.hp.it.perf.ac.app.hpsc.realtime;

import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.service.AbstractServiceProvider;
import com.hp.it.perf.ac.core.service.AcServiceConfig;
import com.hp.it.perf.ac.core.service.AcServiceMetaData;
import com.hp.it.perf.ac.core.service.AcServiceProvider;
import com.hp.it.perf.ac.core.service.DefaultAcServiceMetaData;

public class RealtimeServiceProvider extends AbstractServiceProvider implements
		AcServiceProvider {

	@Override
	public AcServiceMetaData metadata() {
		DefaultAcServiceMetaData metadata = new DefaultAcServiceMetaData(
				"hpscRealtime", RealtimeService.class);
		return metadata;
	}

	@Override
	public void init(AcServiceConfig serviceConfig) {
		validateConfig(serviceConfig, "ac.persist.mongo.host");
		validateConfig(serviceConfig, "ac.persist.mongo.port");
		validateConfig(serviceConfig, "ac.persist.mongo.db.name");
		initClassPathApplicationContext(serviceConfig,
				"/spring/service-config.xml");
		//initXmlApplicationContext(serviceConfig, true);
	}

	@Override
	public AcService getService() {
		return getApplicationContext().getBean(RealtimeService.class);
	}

}
