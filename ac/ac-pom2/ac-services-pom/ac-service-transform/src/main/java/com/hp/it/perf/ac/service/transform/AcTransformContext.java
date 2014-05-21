package com.hp.it.perf.ac.service.transform;

import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.core.AcCoreRuntime;

public interface AcTransformContext {

	public AcCoreRuntime getCoreRuntime();

	public void collect(AcCommonDataWithPayLoad... commonData);

	public String getTransformName();

	public String[] getKeys();

	public String getProperty(String key);

}