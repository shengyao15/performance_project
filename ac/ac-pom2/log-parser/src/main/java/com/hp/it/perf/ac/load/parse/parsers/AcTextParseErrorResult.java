package com.hp.it.perf.ac.load.parse.parsers;

import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcTextElement;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;

public class AcTextParseErrorResult extends AcTextParseResult {
	private String message;
	private Throwable cause;
	private boolean insufficientError;
	private AcParseException parseError;
	private int insufficentExpected;

	public AcTextParseErrorResult(AcParseException e) {
		parseError = e;
	}

	public AcTextParseErrorResult() {
	}

	@Override
	public AcTextElement getElement() {
		throwError();
		return null;
	}

	@Override
	public CharSequence getSource() {
		throwError();
		return null;
	}

	@Override
	public boolean isExactMatch() {
		throwError();
		return false;
	}

	private void throwError() {
		throw new IllegalStateException("Error Parse Result.",
				createParseError());
	}

	public AcParseException createParseError() {
		if (parseError != null) {
			return parseError;
		}
		if (insufficientError) {
			return new AcParseInsufficientException(message);
		} else {
			return new AcParseException(message, cause);
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	public boolean hasError() {
		return true;
	}

	public void setInsufficientError(boolean insufficientError) {
		this.insufficientError = insufficientError;
	}

	public boolean isInsufficientError() {
		return insufficientError;
	}

	public int getInsufficentExpected() {
		return insufficentExpected;
	}

	public void setInsufficentExpected(int insufficentExpected) {
		this.insufficentExpected = insufficentExpected;
	}

}
