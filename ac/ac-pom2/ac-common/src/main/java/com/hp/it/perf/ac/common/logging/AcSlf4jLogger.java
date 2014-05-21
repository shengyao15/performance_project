package com.hp.it.perf.ac.common.logging;

import org.slf4j.Logger;

class AcSlf4jLogger extends AcLogger {

	private final Logger logger;

	AcSlf4jLogger(Logger slf4jLogger) {
		logger = slf4jLogger;
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public void debug(String msg) {
		logger.debug(msg);
	}

	@Override
	public void debug(String msg, Object parameter) {
		logger.debug(msg, parameter);
	}

	@Override
	public void debug(String msg, Object... parameters) {
		logger.debug(msg, parameters);
	}

	@Override
	public void info(String msg) {
		logger.info(msg);
	}

	@Override
	public void info(String msg, Object parameter) {
		logger.info(msg, parameter);
	}

	@Override
	public void info(String msg, Object... parameters) {
		logger.info(msg, parameters);
	}

	@Override
	public void warn(String msg) {
		logger.warn(msg);
	}

	@Override
	public void warn(String msg, Object parameter) {
		logger.warn(msg, parameter);
	}

	@Override
	public void warn(String msg, Object... parameters) {
		logger.warn(msg, parameters);
	}

	@Override
	public void error(String msg) {
		logger.error(msg);
	}

	@Override
	public void error(String msg, Object parameter) {
		logger.error(msg, parameter);
	}

	@Override
	public void error(String msg, Object... parameters) {
		logger.error(msg, parameters);
	}

	@Override
	public void warn(String msg, Throwable cause) {
		logger.warn(msg, cause);
	}

	@Override
	public void error(String msg, Throwable cause) {
		logger.error(msg, cause);
	}

	@Override
	public void debug(String msg, Throwable cause) {
		logger.debug(msg, cause);
	}

	@Override
	public void info(String msg, Throwable cause) {
		logger.info(msg, cause);
	}

}
