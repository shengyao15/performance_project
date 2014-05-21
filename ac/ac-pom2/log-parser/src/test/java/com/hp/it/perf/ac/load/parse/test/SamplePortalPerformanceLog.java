package com.hp.it.perf.ac.load.parse.test;

import java.util.Arrays;
import java.util.Date;

import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DelimsListTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;
import com.hp.it.perf.ac.load.parse.parsers.RegexTextParser;

@ParserPattern(value = "SPF_PERFORMANCE_LOG", parameters = { @Parameter(value = "{DATE_TIME} [{HPSC_DC_ID}] [{THREAD}] - <{DETAIL_LIST}>") }, parser = DelimsTextParser.class)
class SamplePortalPerformanceLog implements Timeable {

	@ParserPattern("DATE_TIME")
	private Date dateTime;

	@ParserPattern("HPSC_DC_ID")
	private String hpscDiagnosticId;

	@ParserPattern("THREAD")
	private String threadName;

	@ParserPattern(value = "DETAIL_LIST", parameters = {
			@Parameter(name = DelimsListTextParser.DELIMS, value = ","),
			@Parameter(name = AcTextParserConstant.KEY_PATTERN, value = "DETAIL") }, parser = DelimsListTextParser.class)
	private Detail[] detailList;

	@ParserPattern(value = "DETAIL", parameters = {
			@Parameter("^(WSRP_CALL|PROFILE_CALL|GROUPS_CALL|REQUEST)(?:\\[(.*)\\])??:(OK|ERROR|unknown)(?ms:\\[(.*)\\])??=(-?\\d+)$"),
			@Parameter(name = AcTextParserConstant.KEY_NAME, value = "TYPE"),
			@Parameter(name = AcTextParserConstant.KEY_NAME, value = "NAME"),
			@Parameter(name = AcTextParserConstant.KEY_NAME, value = "STATUS"),
			@Parameter(name = AcTextParserConstant.KEY_NAME, value = "STATUS_DETAIL"),
			@Parameter(name = AcTextParserConstant.KEY_NAME, value = "DURATION") }, parser = RegexTextParser.class)
	static class Detail {

		@ParserPattern("NAME")
		private String name;

		private String status;

		private boolean success;

		@ParserPattern("STATUS")
		public void parseStatus(String status) {
			this.status = status;
			this.success = "OK".equals(status);
		}

		@ParserPattern("STATUS_DETAIL")
		private String statusDetail;

		@ParserPattern("DURATION")
		private int duration;

		static enum Type {
			REQUEST, WSRP_CALL, PROFILE_CALL, GROUPS_CALL;
		}

		@ParserPattern("TYPE")
		private Type type;

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public String getStatusDetail() {
			return statusDetail;
		}

		public void setStatusDetail(String statusDetail) {
			this.statusDetail = statusDetail;
		}

		@Override
		public String toString() {
			return String
					.format("Detail [name=%s, status=%s, success=%s, statusDetail=%s, duration=%s, type=%s]",
							name, status, success, statusDetail, duration, type);
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
	public String toString() {
		return String
				.format("SPFPerformanceLog [dateTime=%s, hpscDiagnosticId=%s, threadName=%s, detailList=%s]",
						dateTime, hpscDiagnosticId, threadName,
						Arrays.toString(detailList));
	}

}
