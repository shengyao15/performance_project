package com.hp.it.perf.ac.load.parse.parsers;

class MultipleSpaceTextToken implements TextToken {

	private final String text;
	
	public MultipleSpaceTextToken(String text) {
		this.text = text;
		for (char c : text.toCharArray()) {
			if (!isSpaceChar(c)) {
				throw new IllegalArgumentException("invalid space char 0x"
						+ Integer.toHexString(c) + ": '" + c + "'");
			}
		}
	}

	static boolean isSpaceChar(char c) {
		// return false;
		return c == ' ' || c == '\t';
	}

	public TextMatchResult matchBegin(String input, int offset, int endOffset,
			int minSize) {
		int spaceCount = 0;
		for (int i = offset, n = endOffset; i < n; i++) {
			char c = input.charAt(i);
			if (isSpaceChar(c)) {
				spaceCount++;
			} else {
				break;
			}
		}
		// found at least (min) space
		if (spaceCount >= text.length() && spaceCount >= minSize) {
			return new TextMatchResult(input, offset, offset
					+ (minSize < 0 ? spaceCount : Math.max(text.length(),
							minSize)));
		} else {
			return null;
		}
	}

	public String value() {
		return text;
	}

	public String toString() {
		return "S(" + getMinLength() + ")";
	}

	@Override
	public int getMinLength() {
		return text.length();
	}

	@Override
	public TextMatchResult match(String input, int offset, int endOffset,
			int minSize) {
		int index = offset;
		while (true) {
			for (; index < endOffset; index++) {
				char c = input.charAt(index);
				if (isSpaceChar(c)) {
					break;
				}
			}
			// all are not space
			if (index >= endOffset) {
				return null;
			}
			return matchBegin(input, index, endOffset, minSize);
		}
	}

	@Override
	public TextMatchResult matchEnd(String input, int offset, int endOffset,
			int minSize) {
		int spaceCount = 0;
		for (int i = endOffset - 1; i >= offset; i--) {
			char c = input.charAt(i);
			if (isSpaceChar(c)) {
				spaceCount++;
			} else {
				break;
			}
		}
		if (spaceCount >= text.length() && spaceCount >= minSize) {
			return new TextMatchResult(input, endOffset
					- (minSize < 0 ? spaceCount : Math.max(text.length(),
							minSize)), endOffset);
		} else {
			return null;
		}
	}

}
