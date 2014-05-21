package com.hp.it.perf.ac.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Your input parameters contains invalid data, please check and try again!")
public final class ConflictException extends RuntimeException {

	private static final long serialVersionUID = -8937568760636215912L;

	public ConflictException() {
		super();
	}

	public ConflictException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ConflictException(final String message) {
		super(message);
	}

	public ConflictException(final Throwable cause) {
		super(cause);
	}

}
