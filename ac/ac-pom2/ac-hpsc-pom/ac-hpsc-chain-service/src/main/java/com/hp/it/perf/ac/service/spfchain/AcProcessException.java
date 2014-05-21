package com.hp.it.perf.ac.service.spfchain;

public class AcProcessException extends RuntimeException {

    private static final long serialVersionUID = -7420846824432300921L;

    public AcProcessException() {
	super();
    }

    public AcProcessException(String message, Throwable cause) {
	super(message, cause);
    }

    public AcProcessException(String message) {
	super(message);
    }

    public AcProcessException(Throwable cause) {
	super(cause);
    }

}
