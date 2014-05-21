package com.hp.it.perf.acweb;


public class LoglistForm {
	
	private String category;
	private String endtime;
	private int limit;
	private String name;
	private String starttime;
	private String type;
	
	public String getCategory() {
		return category;
	}
	public String getEndtime() {
		return endtime;
	}
	public int getLimit() {
		return limit;
	}
	public String getName() {
		return name;
	}
	
	public String getStarttime() {
		return starttime;
	}
	public String getType() {
		return type;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}


	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public void setName(String n) {
		this.name = n;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
