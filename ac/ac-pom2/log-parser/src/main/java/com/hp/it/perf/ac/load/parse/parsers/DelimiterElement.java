package com.hp.it.perf.ac.load.parse.parsers;

import java.util.ArrayList;
import java.util.List;

class DelimiterElement implements TextElement {
	private TextToken[] tokens;
	private boolean start;
	private boolean end;
	private String notFoundMessage;
	private String text;
	private int minLen;

	public DelimiterElement() {
	}

	public DelimiterElement(String text) {
		initText(text);
	}

	private static final int TOKEN_TYPE_SPACE = 1;
	private static final int TOKEN_TYPE_OTHER = 2;

	@Override
	public void initText(String text) {
		this.text = text;
		int lastEnd = -1;
		int lastTokenType = TOKEN_TYPE_OTHER;
		List<TextToken> tokenList = new ArrayList<TextToken>();
		for (int i = 0, n = text.length(); i <= n; i++) {
			int tokenType;
			if (i != n) {
				char c = text.charAt(i);
				if (MultipleSpaceTextToken.isSpaceChar(c)) {
					tokenType = TOKEN_TYPE_SPACE;
				} else {
					tokenType = TOKEN_TYPE_OTHER;
				}
			} else {
				tokenType = 0;
			}
			if (lastTokenType != tokenType) {
				// process
				if (lastEnd != -1) {
					String tokenTxt = text.substring(lastEnd, i);
					TextToken token = null;
					if (lastTokenType == TOKEN_TYPE_SPACE) {
						token = new MultipleSpaceTextToken(tokenTxt);
					} else if (lastTokenType == TOKEN_TYPE_OTHER) {
						token = new LiteralTextToken(tokenTxt);
					} else {
						throw new IllegalStateException(
								"invalid last token type: " + lastTokenType
										+ " on index: " + i);
					}
					tokenList.add(token);
				}
				lastTokenType = tokenType;
				lastEnd = i;
			}
			if (lastEnd == -1) {
				lastEnd = 0;
			}
		}
		// fix if no text
		if (text.length() == 0) {
			tokenList.add(new LiteralTextToken(text));
		}
		tokens = tokenList.toArray(new TextToken[tokenList.size()]);
		notFoundMessage = "cannot find next delimiter: " + this;
		for (TextToken token : tokens) {
			minLen += token.getMinLength();
		}
	}

	public void setStart() {
		start = true;
	}

	public void setEnd() {
		end = true;
	}

	public TextMatchResult match(String text, int offset, int endOffset) {
		return match(text, offset, endOffset, 0);
	}

	public TextMatchResult match(String text, int offset, int endOffset,
			int minSize) {
		if (end) {
			return matchEnd(text, offset, endOffset, minSize);
		} else if (start) {
			return matchBegin(text, offset, endOffset, minSize);
		} else {
			return matchInside(text, offset, endOffset, minSize);
		}
	}

	protected TextMatchResult matchInside(String text, int offset,
			int endOffset, int minSize) {
		int start = offset;
		int nonLastMinSize = tokens.length > 1 ? 0 : minSize;
		LOOP: while (start < endOffset) {
			TextMatchResult firstMatchResult = tokens[0].match(text, start,
					endOffset, nonLastMinSize);
			if (firstMatchResult == null) {
				break;
			}
			int lastMinSize = updateMinSize(minSize, firstMatchResult.end()
					- start);
			start = firstMatchResult.end();
			TextMatchResult matchResult = null;
			for (int i = 1; i < tokens.length; i++) {
				TextToken token = tokens[i];
				matchResult = token.matchBegin(text, start, endOffset,
						i == tokens.length - 1 ? lastMinSize : TextToken.MATCH_MORE);
				if (matchResult == null) {
					continue LOOP;
				}
				lastMinSize = updateMinSize(lastMinSize, matchResult.end()
						- start);
				start = matchResult.end();
			}
			if (matchResult == null) {
				return firstMatchResult;
			} else {
				return new TextMatchResult(text, firstMatchResult.start(),
						matchResult.end());
			}
		}
		return null;
	}

	protected TextMatchResult matchEnd(String text, int offset, int endOffset,
			int minSize) {
		String testText = text;
		TextMatchResult matchResult = null;
		int end = endOffset;
		for (int i = tokens.length - 1; i >= 0; i--) {
			TextToken token = tokens[i];
			matchResult = token.matchEnd(testText, offset, end,
					i == 0 ? minSize : TextToken.MATCH_MORE);
			if (matchResult == null) {
				return null;
			}
			testText = testText.substring(0, matchResult.start());
			minSize = updateMinSize(minSize, end - matchResult.start());
			end = matchResult.start();
		}
		return new TextMatchResult(text, matchResult.start(), endOffset);
	}

	public TextMatchResult matchBegin(String text, int offset, int endOffset) {
		return matchBegin(text, offset, endOffset, TextToken.MATCH_MORE);
	}

	public TextMatchResult matchBegin(String text, int offset, int endOffset,
			int minSize) {
		int start = offset;
		TextMatchResult matchResult = null;
		for (int i = 0; i < tokens.length; i++) {
			TextToken token = tokens[i];
			matchResult = token.matchBegin(text, start, endOffset,
					i == tokens.length - 1 ? minSize : TextToken.MATCH_MORE);
			if (matchResult == null) {
				return null;
			}
			minSize = updateMinSize(minSize, matchResult.end() - start);
			start = matchResult.end();
		}
		return new TextMatchResult(text, offset, matchResult.end());
	}

	private int updateMinSize(int minSize, int dec) {
		if (minSize >= 0) {
			minSize -= dec;
			if (minSize < 0) {
				minSize = 0;
			}
		}
		return minSize;
	}

	@Override
	public String textValue() {
		return text;
	}

	public String toString() {
		return (start ? "^" : "") + textValue() + (end ? "$" : "");
	}

	public int getMinLength() {
		return minLen;
	}

	public String getNotFoundMessage() {
		return notFoundMessage;
	}

	public boolean isEnd() {
		return end;
	}

	public boolean isStart() {
		return start;
	}
}