package com.hp.it.perf.ac.app.hpsc.storm.beans;

public class RawDataBean {
	
	private long startTime;
	private int gruanlityType;
	private int category;
	private int type;
	private int totalCount;
	private int errorCount;
	private double score;
	private String message;

	public RawDataBean(long startTime, int gruanlityType, int category,
			int type, int totalCount, int errorCount, double score) {
		this.startTime = startTime;
		this.gruanlityType = gruanlityType;
		this.category = category;
		this.type = type;
		this.totalCount = totalCount;
		this.errorCount = errorCount;
		this.score = score;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setGruanlityType(int gruanlityType) {
		this.gruanlityType = gruanlityType;
	}

	public int getGruanlityType() {
		return gruanlityType;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getCategory() {
		return category;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getScore() {
		return score;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
