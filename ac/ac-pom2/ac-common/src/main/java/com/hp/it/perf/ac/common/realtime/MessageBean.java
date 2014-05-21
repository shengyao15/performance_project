package com.hp.it.perf.ac.common.realtime;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.annotation.TypeAlias;

@Persistent
@TypeAlias("mb")
public class MessageBean implements Serializable {

	private static final long serialVersionUID = 1083729979333875323L;

	@Id
	private String id;

	private int granularity;
	private int category;
	private int featureType;
	private long startTime;
	private String message;
	private int count;
	
	public MessageBean() {
	}
	
	public MessageBean(int granularity, int category, int featureType,
		long startTime, String message, int count) {
		this.granularity = granularity;
		this.category = category;
		this.featureType = featureType;
		this.startTime = startTime;
		this.message = message;
		this.count = count;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
