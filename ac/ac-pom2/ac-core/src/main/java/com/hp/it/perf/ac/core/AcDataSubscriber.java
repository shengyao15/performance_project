package com.hp.it.perf.ac.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AcDataSubscriber {

	String value() default "";// default will set as service id

	int queueSize() default 1000;

	int maxBufferSize() default 1000;

	Class<?> dataType() default AcCommonDataWithPayLoad.class;

	int threadCount() default 1;// default single thread queue

	String branch() default "master";// main branch

	int maxWaitTime() default 1000;

}
