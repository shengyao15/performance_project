package com.hp.it.perf.ac.core.service.intercept.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hp.it.perf.ac.core.service.intercept.AcServiceIntercepted;
import com.hp.it.perf.ac.core.service.intercept.AcServiceLoggingInterceptor;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@AcServiceIntercepted(AcServiceLoggingInterceptor.class)
public @interface AcServiceLogging {
	public enum Level {
		ERROR, WARN, INFO, DEBUG, TRACE
	}

	Level entryLevel() default Level.TRACE;

	Level durationLevel() default Level.DEBUG;

	Level errorLevel() default Level.WARN;

}
