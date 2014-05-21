package com.hp.it.perf.ac.common.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

class AcJdkLogger extends AcLogger {

	private final Logger logger;

	AcJdkLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isLoggable(Level.FINE);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isLoggable(Level.INFO);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isLoggable(Level.WARNING);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isLoggable(Level.SEVERE);
	}

	@Override
	public void debug(String msg) {
		logger.log(Level.FINE, msg);
	}

	@Override
	public void debug(String msg, Object parameter) {
		log(Level.FINE, msg, parameter);
	}

	@Override
	public void debug(String msg, Object... parameters) {
		log(Level.FINE, msg, parameters);
	}

	@Override
	public void info(String msg) {
		log(Level.INFO, msg);
	}

	@Override
	public void info(String msg, Object parameter) {
		log(Level.INFO, msg, parameter);
	}

	@Override
	public void info(String msg, Object... parameters) {
		log(Level.INFO, msg, parameters);
	}

	@Override
	public void warn(String msg) {
		log(Level.WARNING, msg);
	}

	@Override
	public void warn(String msg, Object parameter) {
		log(Level.WARNING, msg, parameter);
	}

	@Override
	public void warn(String msg, Object... parameters) {
		log(Level.WARNING, msg, parameters);
	}

	@Override
	public void error(String msg) {
		log(Level.SEVERE, msg);
	}

	@Override
	public void error(String msg, Object parameter) {
		log(Level.SEVERE, msg, parameter);
	}

	@Override
	public void error(String msg, Object... parameters) {
		log(Level.SEVERE, msg, parameters);
	}

	private String convert(String msg) {
		int paramIndex = 0;
		StringBuilder builder = new StringBuilder(msg.length() + 16);
		int prevIndex = 0;
		int index;
		while ((index = msg.indexOf("{}", prevIndex)) != -1) {
			builder.append(msg.substring(prevIndex, index)).append("{")
					.append(paramIndex++).append("}");
			prevIndex = index + 2;
		}
		builder.append(msg.substring(prevIndex));
		return builder.toString();
	}

	@Override
	public void warn(String msg, Throwable cause) {
		log(Level.WARNING, msg, cause);
	}

	@Override
	public void error(String msg, Throwable cause) {
		log(Level.SEVERE, msg, cause);
	}

	@Override
	public void debug(String msg, Throwable cause) {
		log(Level.FINE, msg, cause);
	}

	@Override
	public void info(String msg, Throwable cause) {
		log(Level.INFO, msg, cause);
	}

	private static class AcLogRecord extends LogRecord {

		private static final long serialVersionUID = 5197575666352718895L;

		private transient boolean needToInferCaller = true;

		private static String FQCN = AcJdkLogger.class.getName();

		public AcLogRecord(Level level, String msg) {
			super(level, msg);
		}

		@Override
		public String getSourceClassName() {
			if (needToInferCaller) {
				inferCaller();
			}
			return super.getSourceClassName();
		}

		@Override
		public String getSourceMethodName() {
			if (needToInferCaller) {
				inferCaller();
			}
			return super.getSourceMethodName();
		}

		@Override
		public void setSourceClassName(String sourceClassName) {
			needToInferCaller = false;
			super.setSourceClassName(sourceClassName);
		}

		@Override
		public void setSourceMethodName(String sourceMethodName) {
			needToInferCaller = false;
			super.setSourceMethodName(sourceMethodName);
		}

		// code from java.util.logging.LogRecord
		private void inferCaller() {
			needToInferCaller = false;
			StackTraceElement stack[] = (new Throwable()).getStackTrace();
			// First, search back to a method in the AcLogger class.
			int ix = 0;
			while (ix < stack.length) {
				StackTraceElement frame = stack[ix];
				String cname = frame.getClassName();
				if (cname.equals(FQCN)) {
					break;
				}
				ix++;
			}
			// Now search for the first frame before the "AcLogger" class.
			while (ix < stack.length) {
				StackTraceElement frame = stack[ix];
				String cname = frame.getClassName();
				if (!cname.equals(FQCN)) {
					setSourceClassName(cname);
					setSourceMethodName(frame.getMethodName());
					return;
				}
				ix++;
			}
		}
	}

	private void log(Level level, String msg, Throwable cause) {
		if (logger.isLoggable(level)) {
			LogRecord lr = new AcLogRecord(level, msg);
			lr.setThrown(cause);
			lr.setLoggerName(logger.getName());
			doLog(lr);
		}
	}

	private void doLog(LogRecord logRecord) {
		logger.log(logRecord);
	}

	private void log(Level level, String msg, Object... parameters) {
		if (logger.isLoggable(level)) {
			LogRecord lr = new AcLogRecord(level, convert(msg));
			lr.setParameters(parameters);
			lr.setLoggerName(logger.getName());
			doLog(lr);
		}
	}

	private void log(Level level, String msg, Object parameter) {
		if (logger.isLoggable(level)) {
			LogRecord lr = new AcLogRecord(level, convert(msg));
			lr.setParameters(new Object[] { parameter });
			lr.setLoggerName(logger.getName());
			doLog(lr);
		}
	}

	private void log(Level level, String msg) {
		if (logger.isLoggable(level)) {
			LogRecord lr = new AcLogRecord(level, msg);
			lr.setLoggerName(logger.getName());
			doLog(lr);
		}
	}

}
