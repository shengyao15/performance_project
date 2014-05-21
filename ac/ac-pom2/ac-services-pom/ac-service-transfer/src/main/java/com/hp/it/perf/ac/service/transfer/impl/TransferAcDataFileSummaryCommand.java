package com.hp.it.perf.ac.service.transfer.impl;

import org.springframework.stereotype.Component;

import com.hp.it.perf.ac.service.transfer.AcTransferCommand;
import com.hp.it.perf.ac.service.transfer.AcTransferException;
import com.hp.it.perf.ac.service.transfer.data.AcDataFileSummary;

@Component
class TransferAcDataFileSummaryCommand implements
		AcTransferCommand<AcDataFileSummary> {

	@Override
	public Class<AcDataFileSummary> getSupportedDataType() {
		return AcDataFileSummary.class;
	}

	@Override
	public Object process(AcDataFileSummary dataObject,
			AcTransferReceiverInfo receiver) throws AcTransferException {
		return null;
	}

}
