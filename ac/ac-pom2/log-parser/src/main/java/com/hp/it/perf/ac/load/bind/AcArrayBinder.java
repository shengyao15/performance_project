package com.hp.it.perf.ac.load.bind;

import java.lang.reflect.Array;

import com.hp.it.perf.ac.load.parse.AcTextElement;

public class AcArrayBinder implements AcBinder {

	private AcBinder typeBinder;

	public AcArrayBinder(AcBinder typeBinder) {
		this.typeBinder = typeBinder;
	}

	@Override
	public Object create(Object... parameters) throws AcBindingException {
		return Array.newInstance(typeBinder.getBindingType(),
				(Integer) parameters[0]);
	}

	@Override
	public Object bindProperty(Object bean, Object key, AcTextElement element)
			throws AcBindingException {
		Object value = element.bind(typeBinder);
		Array.set(bean, (Integer) key, value);
		return value;
	}

	@Override
	public Class<?> getBindingType() {
		return Array.newInstance(typeBinder.getBindingType(), 0).getClass();
	}

}
