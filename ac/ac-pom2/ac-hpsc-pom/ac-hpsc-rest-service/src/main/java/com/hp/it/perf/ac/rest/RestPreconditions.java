package com.hp.it.perf.ac.rest;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.hp.it.perf.ac.rest.exceptions.ConflictException;
import com.hp.it.perf.ac.rest.exceptions.ForbiddenException;
import com.hp.it.perf.ac.rest.exceptions.ResourceNotFoundException;

/**
 * Simple static methods to be called at the start of your own methods to verify
 * correct arguments and state. If the Precondition fails, an {@link HttpStatus}
 * code is thrown
 */
public final class RestPreconditions {

	private RestPreconditions() {
		throw new AssertionError();
	}

	/**
	 * Ensures that an object reference passed as a parameter to the calling
	 * method is not null.
	 * 
	 * @param reference
	 *            an object reference
	 * @return the non-null reference that was validated
	 * @throws ResourceNotFoundException
	 *             if {@code reference} is null
	 */
	public static <T> T checkNotNull(final T reference) {
		if (reference == null) {
			throw new ResourceNotFoundException();
		}
		return reference;
	}

	public static <T> List<T> checkNotEmpty(final List<T> reference) {
		if (reference == null || reference.isEmpty()) {
			throw new ResourceNotFoundException();
		}
		return reference;
	}

	public static Object[] checkNotEmpty(final Object[] reference) {
		if (reference == null || reference.length == 0) {
			throw new ResourceNotFoundException();
		}
		return reference;
	}

	/**
	 * Ensures that an object reference passed as a parameter to the calling
	 * method is not null.
	 * 
	 * @param reference
	 *            an object reference
	 * @return the non-null reference that was validated
	 * @throws ConflictException
	 *             if {@code reference} is null
	 */
	public static <T> T checkRequestElementNotNull(final T reference) {
		if (reference == null) {
			throw new ConflictException();
		}
		return reference;
	}

	/**
	 * Ensures the truth of an expression
	 * 
	 * @param expression
	 *            a boolean expression
	 */
	public static void checkRequestState(final boolean expression) {
		if (!expression) {
			throw new ConflictException();
		}
	}

	/**
	 * Check if some value was found, otherwise throw exception.
	 * 
	 * @param expression
	 *            has value true if found, otherwise false
	 * @throws ResourceNotFoundException
	 *             if expression is false, means value not found.
	 */
	public static void checkFound(final boolean expression) {
		if (!expression) {
			throw new ResourceNotFoundException();
		}
	}

	/**
	 * Check if some value was found, otherwise throw exception.
	 * 
	 * @param expression
	 *            has value true if found, otherwise false
	 * @throws ForbiddenException
	 *             if expression is false, means operation not allowed.
	 */
	public static void checkAllowed(final boolean expression) {
		if (!expression) {
			throw new ForbiddenException();
		}
	}
}