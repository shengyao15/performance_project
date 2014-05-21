package com.hp.it.perf.ac.service.transfer;

import com.hp.it.perf.ac.service.transfer.impl.AcTransferReceiverInfo;

public interface AcTransferCommand<T> {

	public Class<T> getSupportedDataType();

	public Object process(T data, AcTransferReceiverInfo receiver)
			throws AcTransferException;

}
