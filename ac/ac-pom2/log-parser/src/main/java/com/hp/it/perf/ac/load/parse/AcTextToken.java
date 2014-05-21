package com.hp.it.perf.ac.load.parse;

public class AcTextToken {

	private String content;
	private String nextToken;
	// TODO do we really need this flag?
	private boolean endOfLine;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNextToken() {
		return nextToken;
	}

	public void setNextToken(String nextToken) {
		this.nextToken = nextToken;
	}

	public boolean isEndOfLine() {
		return endOfLine;
	}

	public void setEndOfLine(boolean endOfLine) {
		this.endOfLine = endOfLine;
	}

	public static AcTextToken singleLine(String line) {
		AcTextToken token = new AcTextToken();
		token.setContent(line);
		token.setEndOfLine(true);
		token.setNextToken(null);
		return token;
	}

	@Override
	public String toString() {
		return "AcTextToken [content=" + content + ", nextToken=" + nextToken
				+ ", endOfLine=" + endOfLine + "]";
	}

}
