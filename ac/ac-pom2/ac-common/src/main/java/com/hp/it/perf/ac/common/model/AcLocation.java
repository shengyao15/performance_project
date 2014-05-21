package com.hp.it.perf.ac.common.model;

import java.io.Serializable;

public class AcLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7242088518889485459L;

	private String server;

	private String absolutePath;

	private String fileName;

	private int lineNum;

	private long offset;

	private long range;

	public String getAbsolutePath() {
		return absolutePath;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLineNum() {
		return lineNum;
	}

	public long getOffset() {
		return offset;
	}

	public long getRange() {
		return range;
	}

	public String getServer() {
		return server;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public void setRange(long range) {
		this.range = range;
	}

	public void setServer(String server) {
		this.server = server;
	}

}
