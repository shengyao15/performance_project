package com.hp.it.perf.ac.app.hpsc.storm.beans;

import java.io.Serializable;

public class SumCountErrorBean implements Serializable {
	private static final long serialVersionUID = -32973814277276062L;
	private Number sum;
	private int sumCount;
	private int count;
	private int errorCount;
	public SumCountErrorBean(Number sum, int sumCount, int count, int errorCount) {
		this.sum = sum;
		this.sumCount = sumCount;
		this.count = count;
		this.errorCount = errorCount;
	}
	public void setSum(Number sum) {
		this.sum = sum;
	}
	public Number getSum() {
		return sum;
	}
	public void setSumCount(int sumCount) {
		this.sumCount = sumCount;
	}
	public int getSumCount() {
		return sumCount;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCount() {
		return count;
	}
	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}
	public int getErrorCount() {
		return errorCount;
	}
}
