package com.hp.it.perf.ac.service.transfer;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import com.hp.it.perf.ac.common.data.AcData;
import com.hp.it.perf.ac.common.data.AcDataObject;

public class AcTransferSender implements AcSender, Closeable {

	private AcTransferService remote;
	private AcSenderId id = AcSenderId.createId();
	private Integer transferId;

	public AcTransferSender(AcTransferService remote) throws IOException {
		this.remote = remote;
		initSender();
	}

	private void initSender() throws IOException {
		try {
			// TODO header for sender
			transferId = remote.startReceive(id, new Properties());
		} catch (AcTransferException e) {
			// TODO Auto-generated catch block
			throw new IOException(e);
		}
	}

	@Override
	public void sendObject(Serializable data) throws IOException {
		sendData(new AcDataObject(data));
	}

	@Override
	public void sendData(AcData data) throws IOException {
		if (transferId == null) {
			throw new IOException("sender is closed");
		}
		try {
			remote.onReceive(transferId, data);
		} catch (AcTransferException e) {
			// TODO Auto-generated catch block
			throw new IOException(e);
		}
	}

	@Override
	public void close() throws IOException {
		if (transferId == null) {
			throw new IOException("sender is closed");
		}
		try {
			remote.endReceive(transferId);
		} catch (AcTransferException e) {
			// TODO Auto-generated catch block
			throw new IOException(e);
		}
		transferId = null;
	}

}
