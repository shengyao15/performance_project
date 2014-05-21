package com.hp.it.perf.ac.load.parse;

public class AcStopParseException extends AcParseException {

	private static final long serialVersionUID = 890528071934707491L;

	private boolean normalStop = true;

	public AcStopParseException() {
		super();
	}

	public AcStopParseException(String message) {
		super(message);
	}

	public boolean isNormalStop() {
		return normalStop;
	}

	public AcStopParseException setNormalStop(boolean normalStop) {
		this.normalStop = normalStop;
		return this;
	}

}
