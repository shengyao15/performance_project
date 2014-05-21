package com.hp.it.perf.ac.load.util;

import java.util.Arrays;

public class Calculator {

	private long[] sortedValues;

	private long[] values = new long[0];

	private double sum;

	private double sumOfSquares;

	private int size;

	private static final int INC = 1024;

	public static Calculator build(long[] data) {
		Calculator cal = new Calculator();
		cal.values = data.clone();
		cal.size = cal.values.length;
		return cal;
	}

	private Calculator() {
		sum = 0.0D;
		sumOfSquares = 0.0D;
	}

	public synchronized void addData(long data) {
		if (size + 1 > values.length) {
			long[] newData = new long[size + 1 + INC];
			System.arraycopy(values, 0, newData, 0, size);
			values = newData;
		}
		values[size++] = data;
		sortedValues = null;
		sum = 0L;
	}

	public synchronized void addData(long[] data) {
		if (size + data.length > values.length) {
			long[] newData = new long[size + data.length + INC];
			System.arraycopy(values, 0, newData, 0, size);
			values = newData;
		}
		System.arraycopy(data, 0, values, size, data.length);
		size += data.length;
		sortedValues = null;
		sum = 0L;
	}

	public long getMedian() {
		return getPercentPoint(0.5);
	}

	public long getPercentPoint(double percent) {
		buildSortedValues();
		if (getCount() > 0 && percent >= 0.0 && percent <= 1.0) {
			if(percent == 0.0){
				return sortedValues[0];
			}
			return sortedValues[(int) Math.ceil( percent * ((double) getCount()) ) - 1];
			
			//return sortedValues[(int) ((double) getCount() * percent)];
		} else {
			return 0L;
		}
	}

	public double getMean() {
		buildSums();
		return sum / (double) getCount();
	}

	public double getStandardDeviation() {
		buildSums();
		double mean = getMean();
		return Math.sqrt(sumOfSquares / (double) getCount() - mean * mean);
	}

	public long getMin() {
		buildSortedValues();
		if (getCount() > 0) {
			return sortedValues[0];
		} else {
			return Long.MIN_VALUE;
		}
	}

	public int getCount() {
		return size;
	}

	public long getMax() {
		buildSortedValues();
		if (getCount() > 0) {
			return sortedValues[getCount() - 1];
		} else {
			return Long.MAX_VALUE;
		}
	}

	public long getSum() {
		buildSums();
		return new Double(sum).longValue();
	}

	private void buildSums() {
		if (sum == 0L) {
			for (int i = 0; i < getCount(); i++) {
				long l = values[i];
				double currentVal = l;
				sum += currentVal;
				sumOfSquares += currentVal * currentVal;
			}
		}
	}

	private void buildSortedValues() {
		if (sortedValues == null) {
			sortedValues = values; // save space without clone
			Arrays.sort(sortedValues, 0, getCount());
		}
	}

	public String toString() {
		return String
				.format(
						"count %d; mean %,3.1f; min %,d; max %,d; mid %,d; 90%%-line %,d; sum %,d",
						getCount(), getMean(), getMin(), getMax(), getMedian(),
						getPercentPoint(0.9), getSum());
	}

	public long[] getBottoms(int size) {
		long[] data = new long[Math.min(size, getCount())];
		buildSortedValues();
		System.arraycopy(sortedValues, 0, data, 0, data.length);
		return data;
	}

	public long[] getTops(int size) {
		long[] data = new long[Math.min(size, getCount())];
		buildSortedValues();
		for (int i = 0; i < data.length; i++) {
			data[i] = sortedValues[getCount() - i - 1];
		}
		return data;
	}

}
