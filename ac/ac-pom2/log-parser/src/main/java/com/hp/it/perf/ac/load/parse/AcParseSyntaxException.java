package com.hp.it.perf.ac.load.parse;

public class AcParseSyntaxException extends IllegalArgumentException {

	private static final long serialVersionUID = -775895821841505423L;

	public AcParseSyntaxException() {
		super();
	}

	public AcParseSyntaxException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcParseSyntaxException(String message) {
		super(message);
	}

	public AcParseSyntaxException(Throwable cause) {
		super(cause);
	}

}
