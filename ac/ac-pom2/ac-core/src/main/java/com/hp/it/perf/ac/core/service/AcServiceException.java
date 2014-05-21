package com.hp.it.perf.ac.core.service;

import com.hp.it.perf.ac.core.AcCoreException;

public class AcServiceException extends AcCoreException {

	private static final long serialVersionUID = -3781339820729786774L;
	private String serviceId;

	public AcServiceException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AcServiceException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public AcServiceException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AcServiceException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public AcServiceException setServiceId(String serviceId) {
		this.serviceId = serviceId;
		return this;
	}

	@Override
	public String getMessage() {
		return serviceId == null ? super.getMessage()
				: ("[" + serviceId + "] " + super.getMessage());
	}
}
