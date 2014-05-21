package com.hp.it.perf.ac.load.content;

public class AcContentLineInfo {

	private int lineNum;

	private int mutilLine;

	private long offset;

	private int length;

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public int getMutilLine() {
		return mutilLine;
	}

	public void setMutilLine(int mutilLine) {
		this.mutilLine = mutilLine;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public String toString() {
		return String.format(
				"[lineNum=%s, mutilLine=%s, offset=%s, length=%s]", lineNum,
				mutilLine, offset, length);
	}

}
