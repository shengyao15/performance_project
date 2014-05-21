package com.hp.it.perf.ac.service.transfer;

import java.io.IOException;
import java.io.Serializable;

import com.hp.it.perf.ac.common.data.AcData;

public interface AcSender {

	public void sendObject(Serializable data) throws IOException;

	public void sendData(AcData data) throws IOException;

}
