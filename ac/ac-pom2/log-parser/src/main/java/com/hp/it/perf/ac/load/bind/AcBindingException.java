package com.hp.it.perf.ac.load.bind;

import com.hp.it.perf.ac.load.content.AcLoadException;

public class AcBindingException extends AcLoadException {

	private static final long serialVersionUID = 5993760240909124133L;

	public AcBindingException() {
	}

	public AcBindingException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcBindingException(String message) {
		super(message);
	}

	public AcBindingException(Throwable cause) {
		super(cause);
	}

}
