package com.hp.it.perf.ac.core;

import com.hp.it.perf.ac.common.core.AcDataListener;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;

public interface AcDispatchRegistry {

	public void register(String name,
			AcDataListener<AcCommonDataWithPayLoad> downstream,
			AcDispatchInfo info);

	public void unregister(String name);

	public String[] getNames();

	public AcDataListener<AcCommonDataWithPayLoad> getDataListener(String name);

	public AcDispatchInfo getDispatchInfo(String name);

	public void processRegister(Object bean, String defaultName);

}
