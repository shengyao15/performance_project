package com.hp.it.perf.ac.load.bind;

public interface AcBindingAccessor {

	public AcBinder getTypeBuilder();

	public void invokeAccessor(Object bean, Object value)
			throws AcBindingException;
}
