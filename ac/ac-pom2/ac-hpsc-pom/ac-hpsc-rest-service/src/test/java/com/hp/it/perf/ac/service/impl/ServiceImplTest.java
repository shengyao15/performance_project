package com.hp.it.perf.ac.service.impl;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary;
import com.hp.it.perf.ac.app.hpsc.search.service.HpscQueryService;
import com.hp.it.perf.ac.common.model.AcidHelper;
import com.hp.it.perf.ac.service.chain.ChainService;
import com.hp.it.perf.ac.service.data.AcRepositoryService;
import com.hp.it.perf.ac.rest.mock.dao.MockChainService;
import com.hp.it.perf.ac.rest.mock.dao.MockHpscQuery;
import com.hp.it.perf.ac.rest.mock.dao.MockRepositoryService;
import com.hp.it.perf.ac.rest.service.impl.ServiceImpl;

public class ServiceImplTest {

	private ServiceImpl service;

	ChainService mockChainService = new MockChainService();
	HpscQueryService mockHpscQuery = new MockHpscQuery();
	AcRepositoryService mockRepositoryService = new MockRepositoryService();

	@Before
	public void setUp() throws Exception {
		service = new ServiceImpl();
		service.setChainService(mockChainService);
		service.setRepositoryService(mockRepositoryService);
	}

	@Test
	public void testGetFullChainWithValidAcid() {
		int profile = 1;
		int sid = 2;
		int category = HpscDictionary.INSTANCE.category("SPFPerformanceLog")
				.code();
		int wsrp_category = HpscDictionary.INSTANCE.category(
				"SPFPerformanceLogDetail").code();
		int type = HpscDictionary.INSTANCE.category("SPFPerformanceLog")
				.type("REQUEST").code();
		int wsrp_type = HpscDictionary.INSTANCE
				.category("SPFPerformanceLogDetail").type("WSRP_CALL").code();
		int level = 1;

		// init test data
		long acid = AcidHelper.getInstance().getAcid(profile, sid, category,
				type, level);
		System.out.println(acid);
//		com.hp.it.perf.ac.rest.model.ChainEntry chain = service
//				.getFullChain(acid);
//		System.out.println(chain.toString());
//		Assert.assertEquals(acid, chain.getAcid());
	}
}
