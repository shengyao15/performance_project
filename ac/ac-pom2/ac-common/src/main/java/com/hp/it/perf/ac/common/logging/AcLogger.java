package com.hp.it.perf.ac.common.logging;

public abstract class AcLogger {

	private static final AcLoggerFactory loggerFactory;

	static {
		AcLoggerFactory slf4jFactory = detect(AcSlf4jLoggerFactory.class, false);
		if (slf4jFactory == null) {
			loggerFactory = detect(AcJdkLoggerFactory.class, true);
		} else {
			loggerFactory = slf4jFactory;
		}
	}

	private static AcLoggerFactory detect(
			Class<? extends AcLoggerFactory> clasz, boolean throwError) {
		try {
			AcLoggerFactory factory = clasz.newInstance();
			factory.getLogger(AcLogger.class.getName());
			return factory;
		} catch (Error e) {
			if (throwError) {
				throw e;
			} else {
				return null;
			}
		} catch (InstantiationException e) {
			throw new Error(e);
		} catch (IllegalAccessException e) {
			throw new Error(e);
		}
	}

	public static AcLogger getLogger(Class<?> logClass) {
		return getLogger(logClass.getName());
	}

	public static AcLogger getLogger(String logName) {
		return loggerFactory.getLogger(logName);
	}

	public abstract boolean isDebugEnabled();

	public abstract boolean isInfoEnabled();

	public abstract boolean isWarnEnabled();

	public abstract boolean isErrorEnabled();

	public abstract void debug(String msg);

	public abstract void debug(String msg, Object parameter);

	public abstract void debug(String msg, Object... parameters);

	public abstract void debug(String msg, Throwable cause);

	public abstract void info(String msg);

	public abstract void info(String msg, Object parameter);

	public abstract void info(String msg, Object... parameters);

	public abstract void info(String msg, Throwable cause);

	public abstract void warn(String msg);

	public abstract void warn(String msg, Object parameter);

	public abstract void warn(String msg, Object... parameters);

	public abstract void warn(String msg, Throwable cause);

	public abstract void error(String msg);

	public abstract void error(String msg, Object parameter);

	public abstract void error(String msg, Object... parameters);

	public abstract void error(String msg, Throwable cause);

}
