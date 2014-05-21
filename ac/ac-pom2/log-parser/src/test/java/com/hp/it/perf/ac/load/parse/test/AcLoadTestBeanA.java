package com.hp.it.perf.ac.load.parse.test;

import java.util.Arrays;
import java.util.Date;

import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.ConstantTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsListTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;
import com.hp.it.perf.ac.load.parse.parsers.RegexTextParser;

//@ParserPattern(value = "PERF", parameters = {
//"(?ms)\\| (.*?) \\| (\\d+?) \\| (\\d+?) \\| (.*?) \\| (.*?) \\| ([0-9.]+?) \\| (.*?) \\| (.*?)",
//"DATE_TIME", "TRANSACTION_ID", "SESSION_ID", "PORTLET_NAME",
//"PORTLET_METHOD", "DURATION", "PERF_DETAIL_LIST", "MESSAGE" }, parser = RegexTextParser.class)
@ParserPattern(value = "PERF", parameters = { @Parameter("| {DATE_TIME} | {TRANSACTION_ID} | {SESSION_ID} | {PORTLET_NAME} | {PORTLET_METHOD} | {DURATION} | {PERF_DETAIL_LIST} | {MESSAGE}") }, parser = DelimsTextParser.class)
class AcLoadTestBeanA {

	@ParserPattern(value = "PERF_DETAIL", parameters = { @Parameter("\\{{DETAIL_TYPE}({DETAIL_NAME}):{DURATION}\\}") }, parser = DelimsTextParser.class)
	static class DetailBean {

		@ParserPattern("DETAIL_TYPE")
		private String type;

		@ParserPattern("DETAIL_NAME")
		private String name;

		private int duration;

		@ParserPattern(value = "DURATION")
		public void parseDuration(Number duration) {
			this.duration = (int) (duration.doubleValue() * 1000);
		}

		@Override
		public String toString() {
			return "DetailBean [type=" + type + ", name=" + name
					+ ", duration=" + duration + "]";
		}

	}

	@ParserPattern(value = "DATE_TIME")
	private Date dateTime;

	@ParserPattern(value = "TRANSACTION_ID")
	private long transactionId;

	private String sessionId;

	@ParserPattern(value = "SESSION_ID", parameters = { @Parameter("(\\d\\d)(\\d\\d)(\\d+)") }, parser = RegexTextParser.class)
	public void parseSessionId(String part1, String part2, String part3) {
		sessionId = part1 + part2 + part3;
	}

	@ParserPattern("PORTLET_NAME")
	private String portletName;

	@ParserPattern(value = "PORTLET_METHOD", parameters = {
			@Parameter("RENDER_PHASE"), @Parameter("ACTION_PHASE"),
			@Parameter("RESOURCE_PHASE"), @Parameter("DUMMY | _PHASE") }, parser = ConstantTextParser.class)
	private String portletMethod;

	private int duration;

	@ParserPattern(value = "MESSAGE", parameters = { @Parameter(AcTextParserConstant.MATCH_ANY) })
	private String message;

	@ParserPattern(value = "PERF_DETAIL_LIST", parameters = {
			@Parameter(","),
			@Parameter(name = AcTextParserConstant.KEY_PATTERN, value = "PERF_DETAIL") }, parser = DelimsListTextParser.class)
	private DetailBean[] detailList;

	@ParserPattern(value = "DURATION")
	public void parseDuration(Number duration) {
		this.duration = (int) (duration.doubleValue() * 1000);
	}

	public DetailBean[] getDetailList() {
		return detailList;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return String
				.format("AcLoadTestBeanA [dateTime=%s, transactionId=%s, sessionId=%s, portletName=%s, portletMethod=%s, duration=%s, detailList=%s, message=%s]",
						dateTime, transactionId, sessionId, portletName,
						portletMethod, duration, Arrays.toString(detailList),
						message);
	}

}
