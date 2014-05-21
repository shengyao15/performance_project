package com.hp.it.perf.ac.service.transform;

public interface AcTransformer {

	public void transform(Object source, AcTransformContext collector)
			throws AcTransformException;

	public String getDefaultName();

}
