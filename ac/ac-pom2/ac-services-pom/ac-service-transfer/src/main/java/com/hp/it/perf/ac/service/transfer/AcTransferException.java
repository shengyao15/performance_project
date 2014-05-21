package com.hp.it.perf.ac.service.transfer;

import com.hp.it.perf.ac.common.model.AcCommonException;

public class AcTransferException extends AcCommonException {

	private static final long serialVersionUID = 1L;

	public AcTransferException() {
		super();
	}

	public AcTransferException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcTransferException(String message) {
		super(message);
	}

	public AcTransferException(Throwable cause) {
		super(cause);
	}

}
