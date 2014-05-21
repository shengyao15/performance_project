package com.hp.it.perf.ac.load.parse.plugins;

import com.hp.it.perf.ac.load.parse.AcParseException;

public class AcParsePluginException extends AcParseException {

	private static final long serialVersionUID = -8614976974217768894L;

	public AcParsePluginException() {
		super();
	}

	public AcParsePluginException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcParsePluginException(String message) {
		super(message);
	}

	public AcParsePluginException(Throwable cause) {
		super(cause);
	}

}
