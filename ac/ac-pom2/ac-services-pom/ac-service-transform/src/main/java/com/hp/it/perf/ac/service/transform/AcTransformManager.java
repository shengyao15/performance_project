package com.hp.it.perf.ac.service.transform;

import com.hp.it.perf.ac.common.model.AcCommonException;

public interface AcTransformManager {

	public void registerTransformer(AcTransformer transformer, String name)
			throws AcCommonException;

	public void unregisterTransformer(String name) throws AcCommonException;

	public String[] getTransformerNames();

	public AcTransformer getTransformer(String name) throws AcCommonException;

}
