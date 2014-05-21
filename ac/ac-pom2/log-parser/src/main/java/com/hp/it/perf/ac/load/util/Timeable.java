package com.hp.it.perf.ac.load.util;

import java.util.Date;

public interface Timeable {

	public static class Comparator implements Comparable<Object> {

		private Date startTime;

		private Date endTime;

		public Comparator(Date startTime, Date endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
		}

		public Date getStartTime() {
			return startTime;
		}

		public Date getEndTime() {
			return endTime;
		}

		public int compareDate(Date date) {
			if (date != null && startTime != null && date.before(startTime)) {
				return 1;
			}
			if (date != null && endTime != null && date.after(endTime)) {
				return -1;
			}
			return 0;
		}

		@Override
		public int compareTo(Object o) {
			if (o instanceof Timeable
					&& (startTime != null || endTime != null)) {
				Timeable timeableBean = (Timeable) o;
				if (timeableBean != null) {
					Date date = timeableBean.getDateTime();
					return compareDate(date);
				}
			}
			return 0;
		}

	}

	public Date getDateTime();
}
