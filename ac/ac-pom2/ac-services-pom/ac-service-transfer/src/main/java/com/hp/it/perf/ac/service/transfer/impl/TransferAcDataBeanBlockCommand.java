package com.hp.it.perf.ac.service.transfer.impl;

import java.lang.reflect.Array;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.hp.it.perf.ac.service.transfer.AcTransferCommand;
import com.hp.it.perf.ac.service.transfer.AcTransferException;
import com.hp.it.perf.ac.service.transfer.data.AcDataBeanBlock;
import com.hp.it.perf.ac.service.transform.AcTransformData;
import com.hp.it.perf.ac.service.transform.AcTransformService;

@Component
class TransferAcDataBeanBlockCommand implements
		AcTransferCommand<AcDataBeanBlock> {

	@Inject
	private AcTransformService transformService;

	@Override
	public Object process(AcDataBeanBlock dataObject,
			AcTransferReceiverInfo receiver) throws AcTransferException {
		AcTransformData sourceData = new AcTransformData();
		Object beans = Array.newInstance(dataObject.getBeanClass(),
				dataObject.getCount());
		System.arraycopy(dataObject.getBeans(), 0, beans, 0,
				dataObject.getCount());
		sourceData.setLoadData((Object[]) beans);
		sourceData.setTransformName(dataObject.getTransformName());
		transformService.receive(sourceData);
		return null;
	}

	@Override
	public Class<AcDataBeanBlock> getSupportedDataType() {
		return AcDataBeanBlock.class;
	}

}
