package com.hp.it.perf.ac.load.bind;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AcMemeberAccessor implements AcBindingAccessor {

	private AccessibleObject accessor;

	private AcBinder typeBinder;

	public AcMemeberAccessor(AccessibleObject accessor) {
		this.accessor = accessor;
		// speed up, and enable private access
		if (!accessor.isAccessible()) {
			accessor.setAccessible(true);
		}
	}

	public void setTypeBuilder(AcBinder typeBinder) {
		this.typeBinder = typeBinder;
	}

	public AcBinder getTypeBuilder() {
		return this.typeBinder;
	}

	public void invokeAccessor(Object bean, Object childValue)
			throws AcBindingException {
		try {
			if (accessor instanceof Field) {
				((Field) accessor).set(bean, childValue);
			} else if (accessor instanceof Method) {
				Method method = (Method) accessor;
				if (method.getParameterTypes().length > 1 && childValue != null
						&& childValue.getClass().isArray()) {
					method.invoke(bean, (Object[]) childValue);
				} else {
					method.invoke(bean, childValue);
				}
			}
		} catch (InvocationTargetException e) {
			throw new AcBindingException("binding invocation error",
					e.getCause());
		} catch (IllegalArgumentException e) {
			throw new AcBindingException("binding error", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("unexpected access error", e);
		}
	}

}
