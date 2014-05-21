package com.hp.it.perf.ac.load.parse.parsers;

// minSize<0: as more as possible
interface TextToken {
	
	int MATCH_MORE = -1;

	public int getMinLength();

	public TextMatchResult match(String input, int offset, int endOffset, int minSize);

	public TextMatchResult matchEnd(String input, int offset, int endOffset, int minSize);

	public TextMatchResult matchBegin(String input, int offset, int endOffset, int minSize);

}