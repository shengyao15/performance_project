package com.hp.it.perf.ac.load.bind;

import java.util.LinkedHashMap;
import java.util.Map;

import com.hp.it.perf.ac.load.parse.AcTextElement;

public class AcMapBinder implements AcBinder {

	private static AcBinder valeBinder = new AcObjectBinder(Object.class);

	@Override
	public Class<?> getBindingType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object create(Object... parameters) throws AcBindingException {
		return new LinkedHashMap<Object, Object>();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object bindProperty(Object bean, Object key, AcTextElement element)
			throws AcBindingException {
		Object value = element.bind(valeBinder);
		Map<Object, Object> map = (Map<Object, Object>) bean;
		map.put(key, value);
		return value;
	}

}
