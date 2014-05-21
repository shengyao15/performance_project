package com.hp.hpsc.logservice.parser.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TopIPAggregateBean implements Serializable {

	public static enum StatisticsGranularities {
		DAY, HALFDAY, HOUR
	}

	public static Date getEndTime(Date start, StatisticsGranularities gran) {
		Date end = null;
		if (start != null) {
			switch (gran) {
			case HOUR:
				end = new Date(start.getTime() + TimeUnit.HOURS.toMillis(1));
				break;
			case HALFDAY:
				end = new Date(start.getTime() + TimeUnit.DAYS.toMillis(1) / 2);
				break;
			case DAY:
			default:
				end = new Date(start.getTime() + TimeUnit.DAYS.toMillis(1));
				break;
			}
		}
		return end;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long count;
	private Date date;
	private String ip;
	private String userAgent;

	public TopIPAggregateBean() {
	}

	public TopIPAggregateBean(String ip, String userAgent, long count, Date d) {
		this.ip = ip;
		this.count = count;
		this.userAgent = userAgent;
		this.date = d;
	}

	public long getCount() {
		return count;
	}

	public Date getDate() {
		return date;
	}

	public String getIp() {
		return ip;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

}
