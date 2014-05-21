package com.hp.hpsc.logview.po;

public class ParserParameters {

	private String content;
	private long lastDate = 0l;
	private long startDate = 0l;
	private String url;
	
	public ParserParameters(){}

	public ParserParameters(String url, String content, long startDate, long lastDate){
		this.url = url;
		this.content = content;
		this.lastDate = lastDate;
		this.startDate = startDate;
	}

	public boolean checkValidDate(long d){
		if(startDate <= d && d <= lastDate){
			return true;
		}
		return false;
	}
	
	public String getContent() {
		return content;
	}

	public long getLastDate() {
		return lastDate;
	}

	public long getStartDate() {
		return startDate;
	}

	public String getUrl() {
		return url;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setLastDate(long lastDate) {
		this.lastDate = lastDate;
	}
	
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
}
