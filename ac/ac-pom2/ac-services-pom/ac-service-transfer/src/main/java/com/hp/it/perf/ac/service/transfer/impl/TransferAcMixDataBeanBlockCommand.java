package com.hp.it.perf.ac.service.transfer.impl;

import java.lang.reflect.Array;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.hp.it.perf.ac.service.transfer.AcTransferCommand;
import com.hp.it.perf.ac.service.transfer.AcTransferException;
import com.hp.it.perf.ac.service.transfer.data.AcMixDataBeanBlock;
import com.hp.it.perf.ac.service.transfer.data.AcMixDataBeanBlock.DataBeanSegment;
import com.hp.it.perf.ac.service.transform.AcTransformData;
import com.hp.it.perf.ac.service.transform.AcTransformService;

@Component
class TransferAcMixDataBeanBlockCommand implements
		AcTransferCommand<AcMixDataBeanBlock> {

	@Inject
	private AcTransformService transformService;

	@Override
	public Object process(AcMixDataBeanBlock mixBlock,
			AcTransferReceiverInfo receiver) throws AcTransferException {
		for (DataBeanSegment dataObject : mixBlock.getSegaments().values()) {
			AcTransformData sourceData = new AcTransformData();
			Object beans = Array.newInstance(dataObject.getBeanClass(),
					dataObject.getCount());
			dataObject.getBeans().toArray((Object[])beans);
			sourceData.setLoadData((Object[]) beans);
			sourceData.setTransformName(dataObject.getTransformName());
			transformService.receive(sourceData);
		}
		return null;
	}

	@Override
	public Class<AcMixDataBeanBlock> getSupportedDataType() {
		return AcMixDataBeanBlock.class;
	}

}
