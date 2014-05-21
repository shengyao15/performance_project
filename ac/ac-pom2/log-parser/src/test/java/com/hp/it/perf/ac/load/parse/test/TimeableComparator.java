package com.hp.it.perf.ac.load.parse.test;

import java.util.Date;

class TimeableComparator implements Comparable<Object> {

	private Date startTime;

	private Date endTime;

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof Timeable && (startTime != null || endTime != null)) {
			Timeable timeableBean = (Timeable) o;
			if (timeableBean != null) {
				Date date = timeableBean.getDateTime();
				if (date != null && startTime != null && date.before(startTime)) {
					return 1;
				}
				if (date != null && endTime != null && date.after(endTime)) {
					return -1;
				}
			}
		}
		return 0;
	}

}
