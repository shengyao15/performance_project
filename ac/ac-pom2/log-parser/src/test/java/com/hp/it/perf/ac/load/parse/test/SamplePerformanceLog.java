package com.hp.it.perf.ac.load.parse.test;

import java.util.Arrays;
import java.util.Date;

import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DelimsListTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;

@ParserPattern(value = "PERFORMANCE_LOG", parameters = { @Parameter("{DATE_TIME} | {TRANSACTION_ID} | {SESSION_ID} | {PORTLET_NAME} | {PORTLET_METHOD} | {DURATION} | {PERFORMANCE_DETAIL_LIST}") }, parser = DelimsTextParser.class)
class SamplePerformanceLog implements Timeable {

	@ParserPattern("DATE_TIME")
	private Date dateTime;

	@ParserPattern("TRANSACTION_ID")
	private long transactionId;

	@ParserPattern("SESSION_ID")
	private long sessionId;

	@ParserPattern("PORTLET_NAME")
	private String portletName;

	@ParserPattern("PORTLET_METHOD")
	private String portletMethod;

	private int duration;

	@ParserPattern(value = "PERFORMANCE_DETAIL_LIST", parameters = {
			@Parameter(name = DelimsListTextParser.DELIMS, value = ","),
			@Parameter(name = AcTextParserConstant.KEY_PATTERN, value = "PERFORMANCE_DETAIL") }, parser = DelimsListTextParser.class)
	private Detail[] details;

	@ParserPattern(value = "PERFORMANCE_DETAIL", parameters = { @Parameter("\\{{NAME}:{DURATION}\\}") }, parser = DelimsTextParser.class)
	static class Detail {

		@ParserPattern("NAME")
		private String name;

		private int duration;

		@ParserPattern("DURATION")
		public void parseDuration(Number duration) {
			this.duration = (int) (duration.doubleValue() * 1000);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

		@Override
		public String toString() {
			return String.format("Detail [name=%s, duration=%s]", name,
					duration);
		}

	}

	@ParserPattern("DURATION")
	public void parseDuration(Number duration) {
		this.duration = (int) (duration.doubleValue() * 1000);
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public String getPortletName() {
		return portletName;
	}

	public void setPortletName(String portletName) {
		this.portletName = portletName;
	}

	public String getPortletMethod() {
		return portletMethod;
	}

	public void setPortletMethod(String portletMethod) {
		this.portletMethod = portletMethod;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Detail[] getDetails() {
		return details;
	}

	public void setDetails(Detail[] details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return String
				.format("PortletPerformanceLog [dateTime=%s, transactionId=%s, sessionId=%s, portletName=%s, portletMethod=%s, duration=%s, details=%s]",
						dateTime, transactionId, sessionId, portletName,
						portletMethod, duration, Arrays.toString(details));
	}

}
