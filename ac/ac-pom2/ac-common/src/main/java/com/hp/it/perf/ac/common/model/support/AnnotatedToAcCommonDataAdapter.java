package com.hp.it.perf.ac.common.model.support;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcCommonException;
import com.hp.it.perf.ac.common.model.AcContext;
import com.hp.it.perf.ac.common.model.AcDictionary;
import com.hp.it.perf.ac.common.model.AcidHelper;
import com.hp.it.perf.ac.common.model.support.AcCommonDataField.Converter;

public class AnnotatedToAcCommonDataAdapter implements ToAcCommonDataAdapter {

	private Map<AcCommonDataField.Field, Map<AccessibleObject, Converter>> fieldAccessors = new EnumMap<AcCommonDataField.Field, Map<AccessibleObject, Converter>>(
			AcCommonDataField.Field.class);

	private AcDictionary dictionary;

	private Class<?> source;

	private void inspect(Class<?> clz) {
		for (Field field : clz.getDeclaredFields()) {
			if (!field.isAnnotationPresent(AcCommonDataField.class)) {
				continue;
			}
			AcCommonDataField dataField = field
					.getAnnotation(AcCommonDataField.class);
			AcCommonDataField.Field fieldName = dataField.value();
			Map<AccessibleObject, Converter> accessor = fieldAccessors
					.get(fieldName);
			if (accessor == null) {
				accessor = new HashMap<AccessibleObject, Converter>();
				fieldAccessors.put(fieldName, accessor);
			}
			// enable reflect easily and performance improvement
			field.setAccessible(true);
			if (!accessor.isEmpty()
					&& fieldName != AcCommonDataField.Field.Context) {
				throw new IllegalArgumentException("duplicate field: "
						+ fieldName + " on " + field);
			}
			accessor.put(
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
			Converter converter;
			if (fieldName == AcCommonDataField.Field.Identifier) {
				// setter for identifier
				if (method.getReturnType() != Void.TYPE) {
					throw new IllegalArgumentException(
							"method with void return value is invalid: "
									+ method);
				}
				if (method.getParameterTypes().length == 0) {
					throw new IllegalArgumentException(
							"method with parameters is invalid: " + method);
				}
				converter = null;
			} else {
				// getter for other
				if (method.getReturnType() == Void.TYPE) {
					throw new IllegalArgumentException(
							"method with void return value is invalid: "
									+ method);
				}
				if (method.getParameterTypes().length > 0) {
					throw new IllegalArgumentException(
							"method with parameters is invalid: " + method);
				}
				converter = newConverter(dataField.converter(), method,
						fieldName.getTargetClass());
			}
			Map<AccessibleObject, Converter> accessor = fieldAccessors
					.get(fieldName);
			if (accessor == null) {
				accessor = new HashMap<AccessibleObject, Converter>();
				fieldAccessors.put(fieldName, accessor);
			}
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			if (!accessor.isEmpty()
					&& fieldName != AcCommonDataField.Field.Context) {
				throw new IllegalArgumentException("duplicate field: "
						+ fieldName + " on " + method);
			}
			accessor.put(method, converter);
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

	protected void invokeWriteAccessor(AccessibleObject accessor, Object bean,
			Converter converter, Object value) throws AcCommonException {
		try {
			// Object setValue = converter.convert(value);
			if (accessor instanceof Field) {
				((Field) accessor).set(bean, value);
			} else if (accessor instanceof Method) {
				Method method = (Method) accessor;
				method.invoke(bean, value);
			} else {
				throw new IllegalStateException("unknown accessor");
			}
		} catch (InvocationTargetException e) {
			throw new AcCommonException("write invocation error", e.getCause());
		} catch (IllegalArgumentException e) {
			throw new AcCommonException("write binding error", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("unexpected access error", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void mapAcCommonData(Object payload, AcCommonData commonData) {
		Map.Entry<AccessibleObject, Converter> writeIdentifierParameter = null;
		for (Map.Entry<AcCommonDataField.Field, Map<AccessibleObject, Converter>> entry : fieldAccessors
				.entrySet()) {
			AcCommonDataField.Field field = entry.getKey();
			for (Map.Entry<AccessibleObject, Converter> entry2 : entry
					.getValue().entrySet()) {
				if (field == AcCommonDataField.Field.Identifier) {
					writeIdentifierParameter = entry2;
				} else {
					Object object = invokeReadAccessor(entry2.getKey(),
							payload, entry2.getValue());
					long acid = commonData.getAcid();
					try {
						AcidHelper acidHelper = AcidHelper.getInstance();
						switch (field) {
						case Name:
							commonData.setName((String) object);
							break;
						case RefIdentifier:
							commonData.setRefAcid((Long) object);
							break;
						case Created:
							long created;
							// test if source is Date based
							if (object instanceof Date) {
								created = ((Date) object).getTime();
							} else {
								created = ((Number) object).longValue();
							}
							commonData.setCreated(created);
							break;
						case Duration:
							commonData.setDuration(((Number) object)
									.intValue());
							break;
						case Category:
							int category = ((Number) object).intValue();
							acid = acidHelper.setCategory(acid, category);
							commonData.setAcid(acid);
							break;
						case Type:
							int type;
							if (object instanceof String) {
								int acCategoryId = acidHelper.getCategory(acid);
								type = dictionary.category(acCategoryId)
										.type((String) object).code();
							} else {
								type = ((Number) object).intValue();
							}
							acid = acidHelper.setType(acid, type);
							commonData.setAcid(acid);
							break;
						case Level:
							int level;
							if (object instanceof String) {
								int acCategoryId = acidHelper.getCategory(acid);
								level = dictionary.category(acCategoryId)
										.level((String) object).code();
							} else {
								level = ((Number) object).intValue();
							}
							acid = acidHelper.setLevel(acid, level);
							commonData.setAcid(acid);
							break;
						case Context:
							if (object instanceof AcContext) {
								commonData.getContexts()
										.add((AcContext) object);
							} else if (object instanceof List) {
								commonData
										.setContexts((List<AcContext>) object);
							} else {
								// maybe ignore null value
								// TODO
							}
							break;
						}
					} catch (Exception e) {
						throw new AcCommonException(
								"set common data field failure: " + field, e);
					}
				}
			}
		}
		// perform identifier write at the end of stage
		if (writeIdentifierParameter != null) {
			invokeWriteAccessor(writeIdentifierParameter.getKey(), payload,
					writeIdentifierParameter.getValue(), commonData.getAcid());
		}
	}

	public void setAnnotatedSource(Class<?> source) {
		this.source = source;
		if (!source.isAnnotationPresent(AcCommonDataEntity.class)) {
			throw new IllegalArgumentException(source + " has no annotation "
					+ AcCommonDataEntity.class);
		}
		inspect(source);
	}

	@Override
	public void toCommonData(Object payload, AcCommonData commonData)
			throws AcCommonException {
		Class<? extends Object> payloadClass = payload.getClass();
		if (payloadClass != source) {
			throw new IllegalArgumentException("payload has not related to "
					+ source);
		}
		mapAcCommonData(payload, commonData);
	}

	@Override
	public void setDictionary(AcDictionary dictionary) {
		this.dictionary = dictionary;
	}

	@Override
	public void setPayloadAcid(Object payload, AcCommonData commonData)
			throws AcCommonException {
		Class<? extends Object> payloadClass = payload.getClass();
		if (payloadClass != source) {
			throw new IllegalArgumentException("payload has not related to "
					+ source);
		}
		// TODO fail if not found?
		for (Map.Entry<AccessibleObject, Converter> entry : fieldAccessors.get(
				AcCommonDataField.Field.Identifier).entrySet()) {
			invokeWriteAccessor(entry.getKey(), payload, entry.getValue(),
					commonData.getAcid());
		}
	}

}
