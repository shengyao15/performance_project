package com.hp.it.perf.ac.load.parse;

import com.hp.it.perf.ac.load.content.AcLoadException;

public class AcParseException extends AcLoadException {

	private static final long serialVersionUID = 5189666398098494404L;

	public AcParseException() {
	}

	public AcParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcParseException(String message) {
		super(message);
	}

	public AcParseException(Throwable cause) {
		super(cause);
	}

}
