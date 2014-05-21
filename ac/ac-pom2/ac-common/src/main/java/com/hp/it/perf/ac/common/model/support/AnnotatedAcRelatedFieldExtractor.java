package com.hp.it.perf.ac.common.model.support;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.it.perf.ac.common.model.AcCommonException;
import com.hp.it.perf.ac.common.model.support.AcCommonDataField.Converter;

public class AnnotatedAcRelatedFieldExtractor implements
		AcRelatedFieldExtractor {

	private Class<?> source;

	private Map<AccessibleObject, Converter> accessors = null;

	public void setAnnotatedSource(Class<?> source) {
		this.source = source;
		if (!source.isAnnotationPresent(AcCommonDataEntity.class)) {
			throw new IllegalArgumentException(source + " has no annotation "
					+ AcCommonDataEntity.class);
		}
		inspect(source);
	}

	private void inspect(Class<?> clz) {
		for (Field field : clz.getDeclaredFields()) {
			if (!field.isAnnotationPresent(AcCommonDataField.class)) {
				continue;
			}
			AcCommonDataField dataField = field
					.getAnnotation(AcCommonDataField.class);
			AcCommonDataField.Field fieldName = dataField.value();
			if (fieldName != AcCommonDataField.Field.Related) {
				continue;
			}
			if (accessors == null) {
				accessors = new HashMap<AccessibleObject, Converter>();
			}
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			accessors.put(
					field,
					newConverter(dataField.converter(), field,
							fieldName.getTargetClass()));
		}
		for (Method method : clz.getDeclaredMethods()) {
			if (!method.isAnnotationPresent(AcCommonDataField.class)) {
				continue;
			}
			AcCommonDataField dataField = method
					.getAnnotation(AcCommonDataField.class);
			AcCommonDataField.Field fieldName = dataField.value();
			if (fieldName != AcCommonDataField.Field.Related) {
				continue;
			}
			Converter converter;
			// getter for other
			if (method.getReturnType() == Void.TYPE) {
				throw new IllegalArgumentException(
						"method with void return value is invalid: " + method);
			}
			if (method.getParameterTypes().length > 0) {
				throw new IllegalArgumentException(
						"method with parameters is invalid: " + method);
			}
			converter = newConverter(dataField.converter(), method,
					fieldName.getTargetClass());
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			if (accessors == null) {
				accessors = new HashMap<AccessibleObject, Converter>();
			}
			accessors.put(method, converter);
		}
	}

	private Converter newConverter(Class<? extends Converter> converterClass,
			AnnotatedElement source, Class<?> targetClass) {
		if (converterClass == Converter.class) {
			return null;
		}
		try {
			Converter converter = converterClass.newInstance();
			converter.setSource(source);
			return converter;
		} catch (Exception e) {
			throw new IllegalArgumentException("cannot create converter: "
					+ converterClass, e);
		}
	}

	protected Object invokeReadAccessor(AccessibleObject accessor, Object bean,
			Converter converter) throws AcCommonException {
		try {
			Object value;
			if (accessor instanceof Field) {
				value = ((Field) accessor).get(bean);
			} else if (accessor instanceof Method) {
				Method method = (Method) accessor;
				value = method.invoke(bean);
			} else {
				throw new IllegalStateException("unknown accessor");
			}
			return converter == null ? value : converter.convert(value);
		} catch (InvocationTargetException e) {
			throw new AcCommonException("read invocation error", e.getCause());
		} catch (IllegalArgumentException e) {
			throw new AcCommonException("read binding error", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("unexpected access error", e);
		}
	}

	@Override
	public Iterator<?> extract(Object payload) throws AcCommonException {
		Class<? extends Object> payloadClass = payload.getClass();
		if (payloadClass != source) {
			throw new IllegalArgumentException("payload has is not related to "
					+ source);
		}
		return extractSubdata(payload);
	}

	public Iterator<?> extractSubdata(Object payload) {
		if (accessors == null) {
			return Collections.emptySet().iterator();
		}
		List<Object> list = new ArrayList<Object>();
		for (Map.Entry<AccessibleObject, Converter> entry : accessors
				.entrySet()) {
			Object object = invokeReadAccessor(entry.getKey(), payload,
					entry.getValue());
			if (object instanceof Iterator) {
				Iterator<?> iter = (Iterator<?>) object;
				while (iter.hasNext()) {
					list.add(iter.next());
				}
			} else if (object instanceof Iterable) {
				for (Object obj : (Iterable<?>) object) {
					list.add(obj);
				}
			} else if (object instanceof Object[]) {
				for (Object obj : (Object[]) object) {
					list.add(obj);
				}
			} else if (object.getClass().isArray()) {
				for (int i = 0, n = Array.getLength(object); i < n; i++) {
					list.add(Array.get(object, i));
				}
			} else {
				throw new IllegalArgumentException(
						"extract sub data cannot to Iterator: "
								+ entry.getKey());
			}
		}
		return list.iterator();
	}
}
