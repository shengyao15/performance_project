package com.hp.it.perf.ac.service.transform;

import com.hp.it.perf.ac.common.model.AcCommonException;

public class AcTransformException extends AcCommonException {

	private static final long serialVersionUID = 1L;

	private String transformerName;

	public AcTransformException() {
		super();
	}

	public AcTransformException(String message, Throwable cause) {
		super(message, cause);
	}

	public AcTransformException(String message) {
		super(message);
	}

	public AcTransformException(Throwable cause) {
		super(cause);
	}

	public AcTransformException setTransformerName(String name) {
		this.transformerName = name;
		return this;
	}

	@Override
	public String getMessage() {
		return transformerName == null ? super.getMessage() : ("["
				+ transformerName + "] " + super.getMessage());
	}

	public String getTransformerName() {
		return transformerName;
	}

}
