package com.hp.it.perf.ac.app.hpsc.search.bean;

import java.io.Serializable;

public class ConsumerHomeInfo implements Serializable {

	private static final long serialVersionUID = -7899198567053667870L;

	private int total;
	
	private long totalRequestURLs;
	
	private int maxTime;
	
	private int minTime;
	
	private int fail;
	
	private double failRate;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public long getTotalRequestURLs() {
		return totalRequestURLs;
	}

	public void setTotalRequestURLs(long totalRequestURLs) {
		this.totalRequestURLs = totalRequestURLs;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public int getMinTime() {
		return minTime;
	}

	public void setMinTime(int minTime) {
		this.minTime = minTime;
	}

	public int getFail() {
		return fail;
	}

	public void setFail(int fail) {
		this.fail = fail;
	}

	public double getFailRate() {
		return failRate;
	}

	public void setFailRate(double failRate) {
		this.failRate = failRate;
	}
	
}
