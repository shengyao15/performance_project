package com.hp.it.perf.ac.load.parse;

import com.hp.it.perf.ac.load.bind.AcBinder;

public class AcProcessorConfig {
	protected AcBinder binder;
	protected AcTextParser parser;

	public AcBinder getBinder() {
		return binder;
	}

	public void setBinder(AcBinder binder) {
		this.binder = binder;
	}

	public AcTextParser getParser() {
		return parser;
	}

	public void setParser(AcTextParser parser) {
		this.parser = parser;
	}

}
