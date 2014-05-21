package com.hp.it.perf.ac.load.process.support;

import java.lang.reflect.Field;

import com.hp.it.perf.ac.load.common.AcMapper;

public class FieldGetter implements AcMapper<Object, Object> {

	private Field field;

	public FieldGetter(Class<?> clasz, String fieldName) {
		initField(clasz, fieldName);
	}

	private void initField(Class<?> clasz, String fieldName) {
		try {
			field = clasz.getDeclaredField(fieldName);
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
		} catch (NoSuchFieldException e) {
			throw new IllegalArgumentException("invalid field name", e);
		}
	}

	@Override
	public Object apply(Object object) {
		if (field.getDeclaringClass().isInstance(object)) {
			try {
				return field.get(object);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("illegal access field", e);
			}
		} else {
			return null;
		}
	}
}
