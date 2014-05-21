package com.hp.it.perf.ac.load.bind;

import com.hp.it.perf.ac.load.parse.AcTextElement;

public class AcObjectBinder implements AcBinder {

	private final Class<?> objectType;

	public AcObjectBinder(Class<?> objectType) {
		this.objectType = objectType;
	}

	@Override
	public Class<?> getBindingType() {
		return objectType;
	}

	@Override
	public Object create(Object... parameters) throws AcBindingException {
		throw new AcBindingException("create is not supported");
	}

	@Override
	public Object bindProperty(Object bean, Object key, AcTextElement element)
			throws AcBindingException {
		throw new AcBindingException("bind property is not supported");
	}

}
