package com.hp.it.perf.ac.load.parse.parsers;

public class LiteralTextToken implements TextToken {
	private final String text;

	public LiteralTextToken(String text) {
		this.text = text;
	}

	public TextMatchResult matchBegin(String input, int offset, int endOffset,
			int minSize) {
		if (input.startsWith(text, offset)
				&& endOffset - offset >= text.length()
				&& minSize <= text.length()) {
			return new TextMatchResult(input, offset, offset + text.length());
		} else {
			return null;
		}
	}

	public String toString() {
		return "T(" + text + ")";
	}

	public int getMinLength() {
		return text.length();
	}

	public TextMatchResult match(String input, int offset, int endOffset,
			int minSize) {
		int index = input.indexOf(text, offset);
		if (index < 0 || endOffset - index < text.length()) {
			return null;
		} else if (text.length() >= minSize) {
			return new TextMatchResult(input, index, text.length() + index);
		} else {
			return null;
		}
	}

	public TextMatchResult matchEnd(String input, int offset, int endOffset,
			int minSize) {
		if (input.startsWith(text, endOffset - text.length())
				&& endOffset - offset >= text.length()
				&& text.length() >= minSize) {
			return new TextMatchResult(input, endOffset - text.length(),
					endOffset);
		}
		return null;
	}
}
