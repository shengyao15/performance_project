package com.hp.it.perf.ac.load.bind;

import com.hp.it.perf.ac.load.parse.AcTextElement;

public interface AcBinder {

	public Class<?> getBindingType();

	public Object create(Object... parameters) throws AcBindingException;

	public Object bindProperty(Object bean, Object key, AcTextElement element)
			throws AcBindingException;

}
