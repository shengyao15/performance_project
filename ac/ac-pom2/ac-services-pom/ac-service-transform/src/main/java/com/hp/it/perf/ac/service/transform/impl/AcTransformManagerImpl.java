package com.hp.it.perf.ac.service.transform.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hp.it.perf.ac.common.model.AcCommonException;
import com.hp.it.perf.ac.service.transform.AcTransformManager;
import com.hp.it.perf.ac.service.transform.AcTransformer;

@Service
public class AcTransformManagerImpl implements AcTransformManager {

	private static final String ROOT_TRANSFORMER_NAME = "";

	private static final Logger log = LoggerFactory
			.getLogger(AcTransformManagerImpl.class);

	private volatile Map<String, AcTransformer> transformers = new HashMap<String, AcTransformer>();

	private ConcurrentMap<String, String> nameMapping = new ConcurrentHashMap<String, String>();

	@Override
	public synchronized void registerTransformer(AcTransformer transformer,
			String name) throws AcCommonException {
		if (transformers.containsKey(name)) {
			throw new AcCommonException("existing transformer: " + name);
		}
		Map<String, AcTransformer> newTransformers = new HashMap<String, AcTransformer>(
				transformers);
		newTransformers.put(name, transformer);
		nameMapping.put(name, name);
		transformers = newTransformers;
	}

	@Override
	public synchronized void unregisterTransformer(String name)
			throws AcCommonException {
		if (!transformers.containsKey(name)) {
			throw new AcCommonException("not found transformer: " + name);
		}
		Map<String, AcTransformer> newTransformers = new HashMap<String, AcTransformer>(
				transformers);
		newTransformers.remove(name);
		transformers = newTransformers;
		nameMapping.remove(name);
	}

	@Override
	public String[] getTransformerNames() {
		return transformers.keySet().toArray(new String[transformers.size()]);
	}

	@Override
	public AcTransformer getTransformer(String name) throws AcCommonException {
		Map<String, AcTransformer> transformerMap = transformers;
		String realName = nameMapping.get(name);
		if (realName == null) {
			realName = setupNameMapping(transformerMap, name);
		}
		return transformerMap.get(realName);
	}

	private String setupNameMapping(Map<String, AcTransformer> transformerMap,
			String name) {
		String fullName = name;
		while (true) {
			if (transformerMap.containsKey(name)) {
				// existing
				nameMapping.put(name, fullName);
				return name;
			}
			// not found, continue find parent
			int index = name.lastIndexOf('.');
			if (index < 0) {
				name = ROOT_TRANSFORMER_NAME; // default root
			} else {
				name = name.substring(0, index);
			}
			String value = nameMapping.get(name);
			if (value != null) {
				return value;
			} else if (name.length() == 0) {
				throw new IllegalArgumentException("no name mapping for: "
						+ fullName);
			}
		}
	}

	@PostConstruct
	public void loadTransformers() {
		log.info("start loading transformers");
		Iterator<AcTransformer> transformers = ServiceLoader.load(
				AcTransformer.class).iterator();
		while (transformers.hasNext()) {
			AcTransformer transformer = transformers.next();
			String defaultName = transformer.getDefaultName();
			if (defaultName != null) {
				registerTransformer(transformer, defaultName);
				log.info("transformer is loaded with name '{}': {}",
						defaultName, transformer);
			} else {
				log.warn("no default name found for transformer: "
						+ transformer);
			}
		}
		String[] transName = getTransformerNames();
		if (transName.length == 0) {
			log.warn("no transformer loaded");
		} else {
			log.info("transformers are loaded: {}", Arrays.toString(transName));
		}
		// add default if not defined for ROOT_TRANSFORMER_NAME name
		if (!nameMapping.containsKey(ROOT_TRANSFORMER_NAME)) {
			registerTransformer(new DefaultAcTransformer(),
					ROOT_TRANSFORMER_NAME);
		}
	}

}
