package com.hp.it.perf.ac.app.hpsc.search.bean;

import java.io.Serializable;

public class WsrpReport implements Serializable {

	private static final long serialVersionUID = 4872731723047385194L;

	private String name;

	private int count;

	private float avg;

	private float min;

	private float max;

	private float std;

	private float ninetyPercent;

	private int error;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public float getAvg() {
		return avg;
	}

	public void setAvg(float avg) {
		this.avg = avg;
	}

	public float getMin() {
		return min;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public float getStd() {
		return std;
	}

	public void setStd(float std) {
		this.std = std;
	}

	public float getNinetyPercent() {
		return ninetyPercent;
	}

	public void setNinetyPercent(float ninetyPercent) {
		this.ninetyPercent = ninetyPercent;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	private Object[] toValue() {
		return new Object[] { name, count, avg, min, max, ninetyPercent, std,
				error };
	}

}
