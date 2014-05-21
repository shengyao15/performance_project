package com.hp.it.perf.ac.app.hpsc.search.bean;

import java.io.Serializable;

public class ProducerReport implements Serializable {

	private static final long serialVersionUID = 4872731723047385194L;

	private String name;

	private int count;

	private int avg;

	private int min;

	private int max;

	private double std;

	private int ninetyPercent;

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

	public int getAvg() {
		return avg;
	}

	public void setAvg(int avg) {
		this.avg = avg;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public double getStd() {
		return std;
	}

	public void setStd(double std) {
		this.std = std;
	}

	public int getNinetyPercent() {
		return ninetyPercent;
	}

	public void setNinetyPercent(int ninetyPercent) {
		this.ninetyPercent = ninetyPercent;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public Object[] toValue() {
		return new Object[] { name, count, avg, min, max, ninetyPercent, std,
				error };
	}

}
