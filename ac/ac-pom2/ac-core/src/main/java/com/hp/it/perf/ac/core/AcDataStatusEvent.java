package com.hp.it.perf.ac.core;

import com.hp.it.perf.ac.common.core.AcStatusEvent;

public class AcDataStatusEvent extends AcStatusEvent {

	private static final long serialVersionUID = 7213460952725810537L;

	public AcDataStatusEvent(AcCoreContext source) {
		super(source);
	}

	public AcCoreContext getCoreContext() {
		return (AcCoreContext) getSource();
	}
}
