package com.hp.it.perf.acweb;

public class ReportForm {

	private String endtime;
	private String starttime;
	private String type;
	private boolean estimateNinety;
	
	public boolean getEstimateNinety() {
		return estimateNinety;
	}

	public void setEstimateNinety(boolean estimateNinety) {
		this.estimateNinety = estimateNinety;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEndtime() {
		return endtime;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

}
