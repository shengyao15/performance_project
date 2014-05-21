package com.hp.it.perf.ac.service.dispatch;

import com.hp.it.perf.ac.common.core.AcDataListener;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.core.AcDispatchInfo;
import com.hp.it.perf.ac.core.AcService;

public interface AcDispatchService extends AcService {

	public void dispatch(AcCommonDataWithPayLoad... data);

	// TODO
	public void closeDispatch();

	public void registerDownstream(String name,
			AcDataListener<AcCommonDataWithPayLoad> downstream,
			AcDispatchInfo info);

	public void unregisterDownstream(String name);

}
