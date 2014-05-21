package com.hp.it.perf.ac.rest.model;

import java.io.Serializable;

public class RealTimeFeatureError implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1443277744742722784L;

	private int granularity;
	private int category;
	private int featureType;
	private String errorType;
	private int count;

	public RealTimeFeatureError() {
	}

	public RealTimeFeatureError(int granularity, int category, int featureType,
			String errorType, int count) {
		this.granularity = granularity;
		this.category = category;
		this.featureType = featureType;
		this.errorType = errorType;
		this.count = count;
	}

	public int getGranularity() {
		return granularity;
	}

	public void setGranularity(int granularity) {
		this.granularity = granularity;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getFeatureType() {
		return featureType;
	}

	public void setFeatureType(int featureType) {
		this.featureType = featureType;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "RealTimeConsumerError [granularity=" + granularity
				+ ", category=" + category + ", featureType=" + featureType
				+ ", errorType=" + errorType + ", count=" + count + "]";
	}
}
