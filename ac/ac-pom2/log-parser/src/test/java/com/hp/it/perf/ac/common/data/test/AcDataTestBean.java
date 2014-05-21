package com.hp.it.perf.ac.common.data.test;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import com.hp.it.perf.ac.common.data.AcData;
import com.hp.it.perf.ac.common.data.AcDataBean;
import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataOutput;

class AcDataTestBean implements AcData, Serializable {
	private Date dateTime;

	private String hpscDiagnosticId;

	private String threadName;

	private long threadId;

	private int sessionId;

	private Level level;

	private double duration;

	private Detail[] detailList;

	private Unknown unknown = new Unknown();

	private AcBean[] acBean = new AcBean[] { new AcBean(), new AcBean(), null };

	@AcDataBean
	static class AcBean implements Serializable {
		private static Random random = new Random();
		private int intSeed = random.nextInt();
		private long longSeed = random.nextLong();
		private Date date = new Date(longSeed + intSeed);

		public int getIntSeed() {
			return intSeed;
		}

		public void setIntSeed(int intSeed) {
			this.intSeed = intSeed;
		}

		public long getLongSeed() {
			return longSeed;
		}

		public void setLongSeed(long longSeed) {
			this.longSeed = longSeed;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((date == null) ? 0 : date.hashCode());
			result = prime * result + intSeed;
			result = prime * result + (int) (longSeed ^ (longSeed >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof AcBean))
				return false;
			AcBean other = (AcBean) obj;
			if (date == null) {
				if (other.date != null)
					return false;
			} else if (!date.equals(other.date))
				return false;
			if (intSeed != other.intSeed)
				return false;
			if (longSeed != other.longSeed)
				return false;
			return true;
		}

	}

	static class Unknown implements Serializable {
		private static Random random = new Random();
		private int seed = random.nextInt();

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + seed;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Unknown))
				return false;
			Unknown other = (Unknown) obj;
			if (seed != other.seed)
				return false;
			return true;
		}

	}

	enum Level {
		DEBUG, INFO, ERROR;
	}

	static class Detail implements AcData, Serializable {
		private String name;

		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Detail))
				return false;
			Detail other = (Detail) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		@Override
		public void toOutput(AcDataOutput out) throws IOException {
			out.writeString(name);
			out.writeString(value);
		}

		@Override
		public void fromInput(AcDataInput in) throws IOException,
				ClassNotFoundException {
			name = in.readString();
			value = in.readString();
		}

	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getHpscDiagnosticId() {
		return hpscDiagnosticId;
	}

	public void setHpscDiagnosticId(String hpscDiagnosticId) {
		this.hpscDiagnosticId = hpscDiagnosticId;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public Detail[] getDetailList() {
		return detailList;
	}

	public void setDetailList(Detail[] detailList) {
		this.detailList = detailList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(acBean);
		result = prime * result
				+ ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + Arrays.hashCode(detailList);
		long temp;
		temp = Double.doubleToLongBits(duration);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime
				* result
				+ ((hpscDiagnosticId == null) ? 0 : hpscDiagnosticId.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + sessionId;
		result = prime * result + (int) (threadId ^ (threadId >>> 32));
		result = prime * result
				+ ((threadName == null) ? 0 : threadName.hashCode());
		result = prime * result + ((unknown == null) ? 0 : unknown.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AcDataTestBean))
			return false;
		AcDataTestBean other = (AcDataTestBean) obj;
		if (!Arrays.equals(acBean, other.acBean))
			return false;
		if (dateTime == null) {
			if (other.dateTime != null)
				return false;
		} else if (!dateTime.equals(other.dateTime))
			return false;
		if (!Arrays.equals(detailList, other.detailList))
			return false;
		if (Double.doubleToLongBits(duration) != Double
				.doubleToLongBits(other.duration))
			return false;
		if (hpscDiagnosticId == null) {
			if (other.hpscDiagnosticId != null)
				return false;
		} else if (!hpscDiagnosticId.equals(other.hpscDiagnosticId))
			return false;
		if (level != other.level)
			return false;
		if (sessionId != other.sessionId)
			return false;
		if (threadId != other.threadId)
			return false;
		if (threadName == null) {
			if (other.threadName != null)
				return false;
		} else if (!threadName.equals(other.threadName))
			return false;
		if (unknown == null) {
			if (other.unknown != null)
				return false;
		} else if (!unknown.equals(other.unknown))
			return false;
		return true;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	@Override
	public void toOutput(AcDataOutput out) throws IOException {
		out.writeString(hpscDiagnosticId);
		out.writeObject(threadName);
		out.writeObject(dateTime);
		out.writeInt(sessionId);
		out.writeLong(threadId);
		out.writeDouble(duration);
		out.writeObject(level);
		out.writeObject(unknown);
		out.writeObject(acBean);
		out.writeInt(detailList.length);
		for (Detail detail : detailList) {
			detail.toOutput(out);
		}
	}

	@Override
	public void fromInput(AcDataInput in) throws IOException,
			ClassNotFoundException {
		hpscDiagnosticId = in.readString();
		threadName = (String) in.readObject();
		dateTime = (Date) in.readObject();
		sessionId = in.readInt();
		threadId = in.readLong();
		duration = in.readDouble();
		level = (Level) in.readObject();
		unknown = (Unknown) in.readObject();
		acBean = (AcBean[]) in.readObject();
		Detail[] details = new Detail[in.readInt()];
		for (int i = 0; i < details.length; i++) {
			details[i] = new Detail();
			details[i].fromInput(in);
		}
		detailList = details;
	}

}
