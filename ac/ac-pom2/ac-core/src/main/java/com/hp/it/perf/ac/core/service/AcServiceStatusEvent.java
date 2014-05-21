package com.hp.it.perf.ac.core.service;

import com.hp.it.perf.ac.common.core.AcStatusEvent;

public class AcServiceStatusEvent extends AcStatusEvent {

	private static final long serialVersionUID = 2558245805292494482L;

	private String serviceId;

	public AcServiceStatusEvent(Object source, String serviceId) {
		super(source);
		this.serviceId = serviceId;
	}

	public String getServiceId() {
		return this.serviceId;
	}

}
