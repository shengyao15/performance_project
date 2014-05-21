package com.hp.it.perf.ac.service.chain;

public class NodeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -1310401339445708632L;

    public NodeNotFoundException() {
	super();
    }

    public NodeNotFoundException(String message, Throwable cause) {
	super(message, cause);
    }

    public NodeNotFoundException(String message) {
	super(message);
    }

    public NodeNotFoundException(Throwable cause) {
	super(cause);
    }

}
