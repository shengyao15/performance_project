package com.hp.it.perf.ac.load.bind;

import org.json.simple.JSONObject;

import com.hp.it.perf.ac.load.parse.AcTextElement;

public class AcJsonObjectBinder extends AcChainedBinder {

	@Override
	public Class<?> getBindingType() {
		return Object.class;
	}

	@Override
	public Object create(Object... parameters) throws AcBindingException {
		return new JSONObject();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object bindProperty(Object bean, Object key, AcTextElement element)
			throws AcBindingException {
		AcChainedBinder childBinder = findBinder((String) key);
		Object value = element.bind(childBinder);
		JSONObject jsonObj = (JSONObject) bean;
		jsonObj.put(childBinder.binderAlias, value);
		return value;
	}

}
