package com.hp.it.perf.ac.rest.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public final class AcBasicObjectMapper extends ObjectMapper {
	private static final Logger log = LoggerFactory
			.getLogger(AcBasicObjectMapper.class);

	public AcBasicObjectMapper() {
		super();
		JsonUtils.enableAcConfiguration(this);
		log.debug("Initialize AcBasicObjectMapper is done.");
	}
}