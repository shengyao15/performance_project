package com.hp.it.perf.ac.app.hpsc.search.bean;

import java.io.Serializable;

public class ConsumerReport implements Serializable {

	private static final long serialVersionUID = 1565132856395590256L;

	private String request;

	private String part;

	private int count;

	private double durAvg;

	private int durMin;

	private int durMax;

	private double durStd;

	private int dur90;

	private int error;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getDurAvg() {
		return durAvg;
	}

	public void setDurAvg(double durAvg) {
		this.durAvg = durAvg;
	}

	public int getDurMin() {
		return durMin;
	}

	public void setDurMin(int durMin) {
		this.durMin = durMin;
	}

	public int getDurMax() {
		return durMax;
	}

	public void setDurMax(int durMax) {
		this.durMax = durMax;
	}

	public double getDurStd() {
		return durStd;
	}

	public void setDurStd(double durStd) {
		this.durStd = durStd;
	}

	public int getDur90() {
		return dur90;
	}

	public void setDur90(int dur90) {
		this.dur90 = dur90;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	private Object[] toValue() {
		return new Object[] { request, part, count, durAvg, durMin, durMax,
		durStd, dur90, error };
	}

}
