package com.hp.it.perf.ac.app.hpsc.search.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class QueryCondition implements Serializable {

	private static final long serialVersionUID = 7090673133365562759L;

	private TimeWindow timeWindow;

	private int category = -1;

	private int type = -1;
	
	private int level = -1;

	private String portlet;

	private String request;

	private String part;

	private String name;

	private int limitCount;

	private String orderBy;

	private List<Long> acids;

	private Paging paging;
	
	private boolean estimateNinety = true;
	
	private boolean queryByDurationDesc = true;

	
	public class TimeWindow implements Serializable {

		private static final long serialVersionUID = -7636937681539872673L;

		private Date startTime;

		private Date endTime;
		
		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public Date getEndTime() {
			return endTime;
		}

		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}
	}

	public class Paging implements Serializable {

		private static final long serialVersionUID = 2295183880959190779L;

		private int start;

		private int end;

		public int getStart() {
			return start;
		}

		public void setStart(int start) {
			this.start = start;
		}

		public int getEnd() {
			return end;
		}

		public void setEnd(int end) {
			this.end = end;
		}

	}

	public TimeWindow getTimeWindow() {
		return timeWindow;
	}

	public void setTimeWindow(TimeWindow timeWindow) {
		this.timeWindow = timeWindow;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getPortlet() {
		return portlet;
	}

	public void setPortlet(String portlet) {
		this.portlet = portlet;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLimitCount() {
		return limitCount;
	}

	public void setLimitCount(int limitCount) {
		this.limitCount = limitCount;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public List<Long> getAcids() {
		return acids;
	}

	public void setAcids(List<Long> acids) {
		this.acids = acids;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public void setEstimateNinety(boolean estimateNinety) {
		this.estimateNinety = estimateNinety;
	}

	public boolean isEstimateNinety() {
		return estimateNinety;
	}

	public void setQueryByDurationDesc(boolean queryByDurationDesc) {
		this.queryByDurationDesc = queryByDurationDesc;
	}

	public boolean isQueryByDurationDesc() {
		return queryByDurationDesc;
	}

}
