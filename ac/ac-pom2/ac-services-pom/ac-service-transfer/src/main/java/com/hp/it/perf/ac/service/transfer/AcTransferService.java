package com.hp.it.perf.ac.service.transfer;

import java.util.Properties;

import com.hp.it.perf.ac.common.data.AcData;
import com.hp.it.perf.ac.core.AcService;

public interface AcTransferService extends AcService {

	// have data in receiver for this profile (and session)

	public int startReceive(AcSenderId senderId, Properties properties)
			throws AcTransferException;

	public AcData onReceive(int rid, AcData data) throws AcTransferException;

	public void endReceive(int rid) throws AcTransferException;

	// get transfer capacity
	public Properties getTransferInfo();

	// internal receiver plugins

	// upload data to dispatch service

	// gateway to get existing data for duplication checking

	// enable on-fly bean parser upgrading

	// make sure to generate ac bean (assign acid)

}
