package com.hp.it.perf.ac.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when user is forbidden to execute specified operation or access
 * specified data.
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "You have no authorization to view the requested resource!")
public class ForbiddenException extends RuntimeException {

	private static final long serialVersionUID = 1005526155412940213L;

	public ForbiddenException() {
		super();
	}

	public ForbiddenException(final String message) {
		super(message);
	}

	public ForbiddenException(final Throwable cause) {
		super(cause);
	}

}