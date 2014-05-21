package com.hp.it.perf.ac.common.realtime;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.annotation.TypeAlias;

@Persistent
@TypeAlias("rtb")
public class RealTimeBean implements Serializable {
	private static final long serialVersionUID = 7818365711250996548L;
	
	@Id
	private String id;
	
	private int granularity;
	private int category;
	private int featureType;
	private int valueType;
	private long startTime;
	private double value;

	public RealTimeBean() {
	}

	public RealTimeBean(int granularity, int category, int featureType,
			int valueType, long startTime, double value) {
		this.granularity = granularity;
		this.category = category;
		this.featureType = featureType;
		this.valueType = valueType;
		this.startTime = startTime;
		this.value = value;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
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

	public int getValueType() {
		return valueType;
	}

	public void setValueType(int valueType) {
		this.valueType = valueType;
	}

	@Override
	public String toString() {
		return String
				.format("RealTimeData [id=%s, granularity=%s, category=%s, featureType=%s, valueType=%s, startTime=%s, value=%s]",
						id, getGranularity(), getCategory(), getFeatureType(),
						getValueType(), getStartTime(), getValue());
	}
}