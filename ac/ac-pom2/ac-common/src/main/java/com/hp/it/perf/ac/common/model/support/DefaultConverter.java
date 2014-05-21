package com.hp.it.perf.ac.common.model.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

import com.hp.it.perf.ac.common.model.support.AcCommonDataField.Converter;

class DefaultConverter implements Converter {

	@SuppressWarnings("unused")
	private AnnotatedElement source;
	private Class<?> targetClass;
	private Class<?> sourceClass;

	public DefaultConverter(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	@Override
	public void setSource(AnnotatedElement source)
			throws IllegalArgumentException {
		this.source = source;
		if (source instanceof Field) {
			sourceClass = ((Field) source).getType();
		} else if (source instanceof Method) {
			sourceClass = ((Method) source).getReturnType();
		}
	}

	@Override
	public Object convert(Object value) {
		if (targetClass.isAssignableFrom(sourceClass)) {
			return value;
		} else {
			if (Date.class.isAssignableFrom(sourceClass)
					&& (Number.class.isAssignableFrom(targetClass) || targetClass
							.isPrimitive())) {
				return ((Date) value).getTime();
			}
			// TODO
			System.err.println(sourceClass + " -> " + targetClass + ": "
					+ value);
			return targetClass.cast(value);
		}
	}

}
