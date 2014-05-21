package com.hp.it.perf.ac.load.parse;

public class AcTextParseResult {
	private AcTextElement element;
	private CharSequence source;
	private boolean exactMatch;

	public AcTextParseResult() {
	}

	public AcTextParseResult(AcTextElement element, CharSequence source) {
		this(element, source, true);
	}

	public AcTextParseResult(AcTextElement element, CharSequence source,
			boolean exactMatch) {
		this.element = element;
		this.source = source;
		this.exactMatch = exactMatch;
	}

	public AcTextElement getElement() {
		return element;
	}

	public void setElement(AcTextElement element) {
		this.element = element;
	}

	public CharSequence getSource() {
		return source;
	}

	public void setSource(CharSequence source) {
		this.source = source;
	}

	public boolean isExactMatch() {
		return exactMatch;
	}

	public void setExactMatch(boolean exactMatch) {
		this.exactMatch = exactMatch;
	}

	public boolean hasError() {
		return false;
	}

}
