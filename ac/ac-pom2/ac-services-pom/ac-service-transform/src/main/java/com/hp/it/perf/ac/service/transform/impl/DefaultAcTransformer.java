package com.hp.it.perf.ac.service.transform.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.service.transform.AcTransformContext;
import com.hp.it.perf.ac.service.transform.AcTransformException;
import com.hp.it.perf.ac.service.transform.AcTransformer;

class DefaultAcTransformer implements AcTransformer {

	private static final Logger log = LoggerFactory
			.getLogger(DefaultAcTransformer.class);

	@Override
	public void transform(Object source, AcTransformContext collector)
			throws AcTransformException {
		String name = collector.getTransformName();
		log.debug("no transformer under name '{}' found for data: {}", name,
				source);
	}

	@Override
	public String getDefaultName() {
		return "";
	}

}
