package com.hp.it.perf.ac.common.logging;

import java.util.logging.Logger;

class AcJdkLoggerFactory implements AcLoggerFactory {

	@Override
	public AcLogger getLogger(String name) {
		return new AcJdkLogger(Logger.getLogger(name));
	}

}
