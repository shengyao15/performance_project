package com.hp.it.perf.ac.service.transfer.impl;

import org.springframework.stereotype.Component;

import com.hp.it.perf.ac.service.transfer.AcTransferCommand;
import com.hp.it.perf.ac.service.transfer.AcTransferException;
import com.hp.it.perf.ac.service.transfer.data.AcDataFileEntry;

@Component
class TransferAcDataFileEntryCommand implements
		AcTransferCommand<AcDataFileEntry> {

	@Override
	public Class<AcDataFileEntry> getSupportedDataType() {
		return AcDataFileEntry.class;
	}

	@Override
	public Object process(AcDataFileEntry dataObject,
			AcTransferReceiverInfo receiver) throws AcTransferException {
		return null;
	}

}
