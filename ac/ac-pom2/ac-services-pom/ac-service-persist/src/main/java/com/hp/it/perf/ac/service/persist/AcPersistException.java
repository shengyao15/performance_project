package com.hp.it.perf.ac.service.persist;

import com.hp.it.perf.ac.common.model.AcCommonException;

public class AcPersistException extends AcCommonException {

	private static final long serialVersionUID = 1L;

	public AcPersistException() {
		super();
	}

	public AcPersistException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcPersistException(String message) {
		super(message);
	}

	public AcPersistException(Throwable cause) {
		super(cause);
	}

}
