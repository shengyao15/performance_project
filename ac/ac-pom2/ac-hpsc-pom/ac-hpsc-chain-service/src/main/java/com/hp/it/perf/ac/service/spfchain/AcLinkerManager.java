package com.hp.it.perf.ac.service.spfchain;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.service.spfchain.AcLinkStrategy.AcLinkStrategyHandler;

class AcLinkerManager {
	
	private static final Logger logger = LoggerFactory.getLogger(AcLinkerManager.class);

	private static interface AcLinkFilter {
		public boolean accept(AcCommonData data);
	}

	private Map<AcLinker, AcLinkFilter> linkers = new HashMap<AcLinker, AcLinkFilter>();

	public void registerLinker(AcLinker linker) {
		for (Annotation ann : linker.getClass().getAnnotations()) {
			if (ann.annotationType() == AcLinkStrategy.class) {
				// strategy class
				AcLinkStrategy acStrategy = (AcLinkStrategy) ann;
				Class<? extends AcLinkStrategyHandler> handler = acStrategy.value();
				if (AcLinkStrategyHandler.class.isAssignableFrom(handler)) {
					registerLinker(linker, createStrategyHandler(handler, null));
				} else {
					throw new IllegalArgumentException();
				}
				return;
			} else if (ann.annotationType().isAnnotationPresent(AcLinkStrategy.class)) {
				// strategy annotation
				AcLinkStrategy acStrategy = ann.annotationType().getAnnotation(AcLinkStrategy.class);
				Class<? extends AcLinkStrategyHandler> handler = acStrategy.value();
				if (AcLinkStrategyHandler.class.isAssignableFrom(handler)) {
					registerLinker(linker, createStrategyHandler(handler, ann));
				} else {
					throw new IllegalArgumentException();
				}
				return;
			}
		}
		registerLinker(linker, null);
	}

	private void registerLinker(AcLinker linker, AcLinkFilter wrapper) {
		linkers.put(linker, wrapper);
	}

	private AcLinkFilter createStrategyHandler(Class<? extends AcLinkStrategyHandler> handlerClass,
			final Object annotation) {
		final AcLinkStrategyHandler handler;
		try {
			handler = (AcLinkStrategyHandler) handlerClass.newInstance();
		} catch (Exception e) {
			throw new AcProcessException(e);
		}
		return new AcLinkFilter() {

			@Override
			public boolean accept(AcCommonData data) {
				return handler.accept(data, annotation);
			}
		};
	}

	public List<AcLinker> getLinkersFor(AcCommonData data) {
		List<AcLinker> result = new ArrayList<AcLinker>();
		for (Map.Entry<AcLinker, AcLinkFilter> entry : linkers.entrySet()) {
			if (entry.getValue() == null || entry.getValue().accept(data)) {
				logger.debug("add linker: {}", entry.getKey());
				result.add(entry.getKey());
			}
		}
		return result;
	}

}
