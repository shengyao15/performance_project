package com.hp.it.perf.ac.load.bind;

import org.json.simple.JSONArray;

import com.hp.it.perf.ac.load.parse.AcTextElement;

public class AcJsonArrayBinder extends AcChainedBinder {

	@Override
	public Class<?> getBindingType() {
		return Object.class;
	}

	@Override
	public Object create(Object... parameters) throws AcBindingException {
		return new JSONArray();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object bindProperty(Object bean, Object key, AcTextElement element)
			throws AcBindingException {
		AcChainedBinder detegate = findBinder(element.getElementName());
		Object value = element.bind(detegate);
		JSONArray jsonArray = (JSONArray) bean;
		jsonArray.add(value);
		return value;
	}

}
