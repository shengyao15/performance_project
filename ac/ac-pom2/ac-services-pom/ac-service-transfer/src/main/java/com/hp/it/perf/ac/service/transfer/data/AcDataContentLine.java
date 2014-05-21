package com.hp.it.perf.ac.service.transfer.data;

import java.io.Serializable;

public class AcDataContentLine implements Serializable {

	private static final long serialVersionUID = 1L;

	private int lineNum;

	private int mutilLine;

	private long offset;

	private int length;

	private String content;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
