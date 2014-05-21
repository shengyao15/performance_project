package com.hp.it.perf.ac.load.parse.parsers;

final class TextMatchResult {

	private final int start;
	private final int end;
	private final String text;

	public TextMatchResult(String text, int start, int end) {
		this.text = text;
		this.start = start;
		this.end = end;
		if (end < start) {
			throw new ArrayIndexOutOfBoundsException(end + "<" + start);
		}
	}

	public int start() {
		return start;
	}

	public int end() {
		return end;
	}

	public String matched() {
		return text.substring(start, end);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Matched Result between index ");
		sb.append(start);
		sb.append(" and ");
		sb.append(end);
		sb.append(" is: ");
		sb.append(matched());
		sb.append('\n');
		sb.append(text);
		sb.append('\n');
		for (int i = 0; i < start; i++)
			sb.append(' ');
		for (int i = start; i < end; i++)
			sb.append('^');
		return sb.toString();
	}
}
