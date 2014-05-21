package com.hp.it.perf.ac.load.content;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

public class AcContentLineReader implements AcContentLineReadable {

	private long lineOffset;

	private String currentLines;

	private String nextLine = null;

	private int currentLineNumber;

	private long currentLineOffset;

	private long nextLineOffset;

	private static final int INIT_LIMIT = 8192; // 8K

	private static final int MAX_LIMIT = 10240; // 10K

	private StringBuilder currentLineBuilder = new StringBuilder(INIT_LIMIT);

	private LineNumberReader reader;

	public AcContentLineReader(Reader in) {
		reader = new LineNumberReader(in);
	}

	protected String readLine() throws IOException {
		String line = internalReadLine();
		if (line != null) {
			lineOffset += line.length() + 1;
		}
		return line;
	}

	protected String internalReadLine() throws IOException {
		return reader.readLine();
	}

	protected int internalgetLineNumber() {
		return reader.getLineNumber();
	}

	public long getLineOffset() {
		return lineOffset;
	}

	public void readLines() throws IOException {
		currentLineOffset = nextLineOffset;
		// make no more expand array inner
		if (currentLineBuilder.length() > MAX_LIMIT) {
			currentLineBuilder = new StringBuilder(INIT_LIMIT);
		} else {
			currentLineBuilder.setLength(0);
		}
		if (nextLine == null) {
			currentLines = readLine();
		} else {
			currentLines = nextLine;
		}
		currentLineBuilder.append(currentLines);
		currentLineNumber = internalgetLineNumber();
		nextLineOffset = lineOffset;
		nextLine = readLine();
	}

	public void readMoreLines() throws IOException {
		if (currentLines == null || nextLine == null) {
			return;
		}
		currentLineBuilder.append('\n').append(nextLine);
		currentLines = currentLineBuilder.toString();
		nextLineOffset = lineOffset;
		nextLine = readLine();
	}

	public AcContentLine getContentLine() {
		if (currentLines == null) {
			return null;
		}
		AcContentLine line = new AcContentLine();
		line.setCurrentLines(currentLines);
		line.setNextLine(nextLine);
		line.setLineInfo(toLineInfo());
		return line;
	}

	private AcContentLineInfo toLineInfo() {
		AcContentLineInfo info = new AcContentLineInfo();
		info.setLineNum(currentLineNumber);
		info.setMutilLine(internalgetLineNumber() - currentLineNumber - 1);
		info.setOffset(currentLineOffset);
		info.setLength(currentLines.length());
		return info;
	}

	public void close() throws IOException {
		reader.close();
	}

	protected void retryNextLine() throws IOException {
		if (nextLine == null) {
			nextLine = readLine();
		}
	}

}