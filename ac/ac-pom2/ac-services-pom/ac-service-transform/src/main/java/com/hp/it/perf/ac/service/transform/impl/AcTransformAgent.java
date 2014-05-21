package com.hp.it.perf.ac.service.transform.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.hp.it.perf.ac.common.core.AcLoadReceiver;
import com.hp.it.perf.ac.core.AcCoreContext;
import com.hp.it.perf.ac.service.dispatch.AcDispatchService;
import com.hp.it.perf.ac.service.transform.AcTransformContext;
import com.hp.it.perf.ac.service.transform.AcTransformData;
import com.hp.it.perf.ac.service.transform.AcTransformManager;
import com.hp.it.perf.ac.service.transform.AcTransformer;

@Component
class AcTransformAgent implements AcLoadReceiver<AcTransformData> {

	@Inject
	private AcTransformManager transformerManager;

	@Inject
	private AcCoreContext acCoreContext;

	@Inject
	private AcTransformPreference preference;

	@Inject
	private AcDispatchService dispatchService;

	@Override
	public void receive(AcTransformData data) {
		String transformName = data.getTransformName();
		AcTransformer transformer = transformerManager
				.getTransformer(transformName);
		AcTransformContext transContext = new DefaultAcTransformContext(
				acCoreContext, dispatchService, preference, transformName);
		for (Object loadData : data.getLoadData()) {
			transformer.transform(loadData, transContext);
		}
	}

}
