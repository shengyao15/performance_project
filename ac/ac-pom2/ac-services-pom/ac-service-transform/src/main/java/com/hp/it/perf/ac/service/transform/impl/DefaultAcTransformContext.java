package com.hp.it.perf.ac.service.transform.impl;

import java.util.Properties;

import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.core.AcCoreContext;
import com.hp.it.perf.ac.core.AcCoreRuntime;
import com.hp.it.perf.ac.service.dispatch.AcDispatchService;
import com.hp.it.perf.ac.service.transform.AcTransformContext;

class DefaultAcTransformContext implements AcTransformContext {

	private final AcCoreContext acCoreContext;
	private String transformName;
	private Properties transformerPreference;
	private final AcDispatchService dispatchService;

	public DefaultAcTransformContext(AcCoreContext acCoreContext,
			AcDispatchService dispatchService,
			AcTransformPreference preference, String transformName) {
		this.acCoreContext = acCoreContext;
		this.dispatchService = dispatchService;
		this.transformName = transformName;
		this.transformerPreference = preference
				.getTransformerPreference(transformName);
		if (this.transformerPreference == null) {
			this.transformerPreference = new Properties();
		}
	}

	@Override
	public AcCoreRuntime getCoreRuntime() {
		return acCoreContext.getCoreRuntime();
	}

	@Override
	public void collect(AcCommonDataWithPayLoad... commonData) {
		dispatchService.dispatch(commonData);
	}

	@Override
	public String getTransformName() {
		return transformName;
	}

	@Override
	public String[] getKeys() {
		return transformerPreference.keySet().toArray(
				new String[transformerPreference.size()]);
	}

	@Override
	public String getProperty(String key) {
		return transformerPreference.getProperty(key);
	}

}
