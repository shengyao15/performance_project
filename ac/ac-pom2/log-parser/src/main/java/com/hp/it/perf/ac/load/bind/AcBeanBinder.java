package com.hp.it.perf.ac.load.bind;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.hp.it.perf.ac.load.parse.AcTextElement;

public class AcBeanBinder implements AcBinder {

	private Constructor<?> accessor;

	private Map<String, AcBindingAccessor> map = new HashMap<String, AcBindingAccessor>();

	public AcBeanBinder(Constructor<?> accessor) {
		this.accessor = accessor;
		if (!accessor.isAccessible()) {
			accessor.setAccessible(true);
		}
	}

	public void addChildBinder(String name, AcMemeberAccessor childBinder) {
		AcBindingAccessor cBinder = map.get(name);
		if (cBinder != null) {
			OneOfBindingAccessor masterAccessor;
			if (!(cBinder instanceof OneOfBindingAccessor)) {
				masterAccessor = new OneOfBindingAccessor();
				masterAccessor.addBinder(cBinder);
				map.put(name, masterAccessor);
			} else {
				masterAccessor = (OneOfBindingAccessor) cBinder;
			}
			masterAccessor.addBinder(childBinder);
		} else {
			map.put(name, childBinder);
		}
	}

	@Override
	public Object create(Object... parameters) throws AcBindingException {
		try {
			return accessor.newInstance(parameters);
		} catch (InvocationTargetException e) {
			throw new AcBindingException("binding invocation error",
					e.getCause());
		} catch (Exception e) {
			throw new AcBindingException("binding get error", e);
		}
	}

	@Override
	public Object bindProperty(Object bean, Object key, AcTextElement element)
			throws AcBindingException {
		AcBindingAccessor childBinder = map.get(key);
		Object childValue = element.bind(childBinder.getTypeBuilder());
		childBinder.invokeAccessor(bean, childValue);
		return childValue;
	}

	public Class<?> getBindingType() {
		return accessor.getDeclaringClass();
	}

}
