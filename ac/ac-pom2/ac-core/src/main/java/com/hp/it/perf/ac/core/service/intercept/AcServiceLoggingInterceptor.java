package com.hp.it.perf.ac.core.service.intercept;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.core.service.intercept.annotation.AcServiceLogging;
import com.hp.it.perf.ac.core.service.intercept.annotation.AcServiceLogging.Level;

public class AcServiceLoggingInterceptor implements AcServiceInterceptor {

	private static final Logger log = LoggerFactory
			.getLogger(AcServiceLoggingInterceptor.class);

	@Override
	public Object invokeService(AcServiceInvokeContext context)
			throws InvocationTargetException, Throwable {
		AcServiceLogging logging = context
				.getInterceptAnnotation(AcServiceLogging.class);
		log(logging.entryLevel(), "Start service method:: {} - {}",
				context.getMethod(), context.getArguments() == null ? "no args"
						: context.getArguments());
		long start = System.nanoTime();
		try {
			return context.proceed();
		} catch (InvocationTargetException t) {
			log(logging.errorLevel(), "{} cause error: {}",
					context.getMethod(), t.getCause());
			throw t;
		} finally {
			long time = System.nanoTime() - start;
			log(logging.entryLevel(), "End service method:: {}",
					context.getMethod());
			log(logging.durationLevel(), "Time on {} :: {} ms",
					context.getMethod(), TimeUnit.NANOSECONDS.toMillis(time));
		}
	}

	private static void log(Level level, String msg, Object... arg) {
		switch (level) {
		case ERROR:
			log.error(msg, arg);
			break;
		case WARN:
			log.warn(msg, arg);
			break;
		case INFO:
			log.info(msg, arg);
			break;
		case DEBUG:
			log.debug(msg, arg);
			break;
		case TRACE:
			log.trace(msg, arg);
			break;
		}
	}

}
