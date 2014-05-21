package com.hp.it.perf.ac.load.parse;

public class AcParseInsufficientException extends AcParseException {

	private static final long serialVersionUID = -1099660911335957338L;

	private int expected;

	public AcParseInsufficientException() {
		super();
	}

	public AcParseInsufficientException(String message) {
		super(message);
	}

	public int getExpected() {
		return expected;
	}

	public void setExpected(int expected) {
		this.expected = expected;
	}

}
