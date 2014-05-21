package com.hp.it.perf.ac.service.transform.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.hp.it.perf.ac.service.transform.AcTransformData;
import com.hp.it.perf.ac.service.transform.AcTransformService;

@Service
class AcTransformServiceImpl implements AcTransformService {

	@Inject
	private AcTransformAgent agent;

	@Override
	public void receive(AcTransformData data) {
		agent.receive(data);
	}

}
