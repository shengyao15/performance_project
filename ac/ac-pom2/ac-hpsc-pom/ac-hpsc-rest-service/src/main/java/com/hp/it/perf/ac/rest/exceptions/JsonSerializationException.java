package com.hp.it.perf.ac.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error occurs when serialize the requestd data to json object!")
public class JsonSerializationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7120269060902737119L;

	public JsonSerializationException() {
		super();
	}

	public JsonSerializationException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	public JsonSerializationException(final String message) {
		super(message);
	}

	public JsonSerializationException(final Throwable cause) {
		super(cause);
	}
}
