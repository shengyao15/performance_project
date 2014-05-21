package com.hp.it.perf.ac.service.transform.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.hp.it.perf.ac.core.service.AcServiceConfig;

@Component
class AcTransformPreference {

	@Inject
	private AcServiceConfig serviceConfig;

	private Map<String, Properties> preferences = new HashMap<String, Properties>();

	public Properties getTransformerPreference(String transformName) {
		return preferences.get(transformName);
	}

	@PostConstruct
	protected void loadPreferences() {
		// TODO
	}

}
