package com.hp.it.perf.ac.common.util;

import java.util.Arrays;

public class Calculator {

    private long[] sortedValues;

    private long[] values = new long[0];

    private double sum;

    private double sumOfSquares;

    public static Calculator build(long[] data) {
        Calculator cal = new Calculator();
        cal.values = data.clone();
        return cal;
    }

    private Calculator() {
        sum = 0.0D;
        sumOfSquares = 0.0D;
    }

    public long getMedian() {
        return getPercentPoint(0.5);
    }

    public long getPercentPoint(double percent) {
        buildSortedValues();
        if (getCount() > 0) {
            return sortedValues[(int) ((double) sortedValues.length * percent)];
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
        return values.length;
    }

    public double getSum() {
        return sum;
    }

    public long getMax() {
        buildSortedValues();
        if (getCount() > 0) {
            return sortedValues[sortedValues.length - 1];
        } else {
            return Long.MAX_VALUE;
        }
    }

    private void buildSums() {
        if (sum == 0L) {
            for (long l : values) {
                double currentVal = l;
                sum += currentVal;
                sumOfSquares += currentVal * currentVal;
            }
        }
    }

    private void buildSortedValues() {
        if (sortedValues == null) {
            sortedValues = values.clone();
            Arrays.sort(sortedValues);
        }
    }

    public String toString() {
        return String.format("count %d; mean %,3.1f; min %,d; max %,d; mid %,d; 90%%line %,d; sum %,3.0f", getCount(),
                getMean(), getMin(), getMax(), getMedian(), getPercentPoint(0.9), getSum());
    }
}
