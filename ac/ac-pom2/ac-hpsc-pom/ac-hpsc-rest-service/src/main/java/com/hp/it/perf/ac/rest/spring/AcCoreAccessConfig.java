package com.hp.it.perf.ac.rest.spring;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary;
import com.hp.it.perf.ac.app.hpsc.realtime.RealtimeService;
import com.hp.it.perf.ac.app.hpsc.search.service.HpscQueryService;
import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.common.model.AcDictionary;
import com.hp.it.perf.ac.core.access.AcCoreAccess;
import com.hp.it.perf.ac.service.chain.ChainService;
import com.hp.it.perf.ac.service.data.AcRepositoryService;
import com.hp.it.perf.ac.service.persist.AcPersistService;
import com.hp.it.perf.ac.service.spfchain.SpfChainService;

@Configuration
public class AcCoreAccessConfig {

	@Inject
	AcCoreAccess coreAccess;

	@Bean(name = "hpscQueryService")
	public HpscQueryService getQueryService() {
		return coreAccess.getService(HpscQueryService.class);
	}

	@Bean(name = "spfchainService")
	public SpfChainService getSpfChainService() {
		return coreAccess.getService(SpfChainService.class);
	}

	@Bean(name = "chainService")
	public ChainService getChainService() {
		return coreAccess.getService(ChainService.class);
	}

	@Bean(name = "repositoryService")
	public AcRepositoryService getRepositoryService() {
		return coreAccess.getService(AcRepositoryService.class);
	}

	@Bean(name = "persistService")
	public AcPersistService getPersistService() {
		return coreAccess.getService(AcPersistService.class);
	}

	@Bean(name = "acSession")
	public AcSession getAcSession() {
		return coreAccess.getSession();
	}

	@Bean(name = "hpscDictionary")
	public AcDictionary getAcDictionary() {
		return HpscDictionary.INSTANCE;
	}
	
	@Bean(name = "realtimeService")
	public RealtimeService getRealtimeService() {
		return coreAccess.getService(RealtimeService.class);
	}
}
