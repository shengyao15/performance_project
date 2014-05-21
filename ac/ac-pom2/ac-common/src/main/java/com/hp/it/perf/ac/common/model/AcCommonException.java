package com.hp.it.perf.ac.common.model;

public class AcCommonException extends RuntimeException {

	private static final long serialVersionUID = 1606442556567798902L;

	public AcCommonException() {
		super();
	}

	public AcCommonException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcCommonException(String message) {
		super(message);
	}

	public AcCommonException(Throwable cause) {
		super(cause);
	}

}
