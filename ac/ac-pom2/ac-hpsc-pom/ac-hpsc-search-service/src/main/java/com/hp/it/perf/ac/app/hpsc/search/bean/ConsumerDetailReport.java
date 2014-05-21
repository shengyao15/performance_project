package com.hp.it.perf.ac.app.hpsc.search.bean;

import java.io.Serializable;

public class ConsumerDetailReport implements Serializable {

	private static final long serialVersionUID = -250531431628987826L;

	private String portletName;

	private int count;

	private int min;

	private int avg;

	private int max;

	private double std;

	private int ninetyPercent;

	private int error;

	public String getPortletName() {
		return portletName;
	}

	public void setPortletName(String portletName) {
		this.portletName = portletName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getAvg() {
		return avg;
	}

	public void setAvg(int avg) {
		this.avg = avg;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getNinetyPercent() {
		return ninetyPercent;
	}

	public void setNinetyPercent(int ninetyPercent) {
		this.ninetyPercent = ninetyPercent;
	}

	public double getStd() {
		return std;
	}

	public void setStd(double std) {
		this.std = std;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	private Object[] toValue() {
		return new Object[] { portletName, count, min, avg, max, ninetyPercent,
		std, error };
	}

}
