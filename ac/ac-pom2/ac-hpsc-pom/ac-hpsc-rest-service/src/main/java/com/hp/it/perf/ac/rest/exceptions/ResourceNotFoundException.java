package com.hp.it.perf.ac.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "It looks like that page you were looking has no content available, sorry!")
public final class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 8213525582713079297L;

	public ResourceNotFoundException() {
		super();
	}

	public ResourceNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ResourceNotFoundException(final String message) {
		super(message);
	}

	public ResourceNotFoundException(final Throwable cause) {
		super(cause);
	}

}
