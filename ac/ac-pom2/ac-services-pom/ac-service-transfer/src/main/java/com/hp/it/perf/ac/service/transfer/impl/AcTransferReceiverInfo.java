package com.hp.it.perf.ac.service.transfer.impl;

import java.util.Properties;

import com.hp.it.perf.ac.service.transfer.AcSenderId;

public class AcTransferReceiverInfo {
	private int receiverId;
	private AcSenderId senderId;
	private Properties senderProperties;

	public AcTransferReceiverInfo(int receiverId, AcSenderId senderId,
			Properties properties) {
		this.receiverId = receiverId;
		this.senderId = senderId;
		this.senderProperties = properties;
	}

	public int getReceiverId() {
		return receiverId;
	}

	public AcSenderId getSenderId() {
		return senderId;
	}

	public Properties getSenderProperties() {
		return senderProperties;
	}

	@Override
	public String toString() {
		return String.format(
				"AcTransferReceiverInfo [receiverId=%s, senderId=%s]",
				receiverId, senderId);
	}

}
