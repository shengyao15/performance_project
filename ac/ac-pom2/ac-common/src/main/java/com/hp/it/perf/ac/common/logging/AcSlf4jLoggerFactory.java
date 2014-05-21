package com.hp.it.perf.ac.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AcSlf4jLoggerFactory implements AcLoggerFactory {

	static {
		try {
			Class.forName("org.slf4j.impl.StaticLoggerBinder");
		} catch (ClassNotFoundException e) {
			throw new NoClassDefFoundError(e.getMessage());
		}
	}

	@Override
	public AcLogger getLogger(String name) {
		Logger logger = LoggerFactory.getLogger(name);
		return new AcSlf4jLogger(logger);
	}

}
