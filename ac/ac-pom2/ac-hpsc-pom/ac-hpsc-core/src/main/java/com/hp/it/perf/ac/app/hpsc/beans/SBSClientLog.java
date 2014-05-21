package com.hp.it.perf.ac.app.hpsc.beans;

import java.util.Arrays;
import java.util.Date;

import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DelimsListTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;
import com.hp.it.perf.ac.load.parse.parsers.RegexTextParser;
import com.hp.it.perf.ac.load.util.Timeable;

@ParserPattern(value = "SBS_CLIENT_LOG", parameters = { @Parameter("{DATE_TIME} {LOG_LEVEL} [{LOG_NAME}] - || {LOG_DETAIL_LIST} ||") }, parser = DelimsTextParser.class)
public class SBSClientLog implements Timeable {

	@ParserPattern(value = "DATE_TIME", parameters = { @Parameter("yyyy-MM-dd HH:mm:ss,SSS") })
	private Date dateTime;

	public enum LogLevel {
		DEBUG, INFO, WARN, ERROR;
	}

	@ParserPattern("LOG_LEVEL")
	private LogLevel logLevel;

	@ParserPattern("LOG_NAME")
	private String loggerName;

	@ParserPattern(value = "LOG_DETAIL_LIST", parameters = {
			@Parameter(name = DelimsListTextParser.DELIMS, value = " | "),
			@Parameter(name = DelimsListTextParser.KEY_PATTERN, value = "LOG_DETAIL") }, parser = DelimsListTextParser.class)
	private LogDetail[] details;

	@ParserPattern(value = "LOG_DETAIL", parameters = { @Parameter("{NAME}: {VALUE}") }, parser = DelimsTextParser.class)
	public static class LogDetail {

		@ParserPattern("NAME")
		private String name;

		private String value;

		private Number duration;

		@ParserPattern(value = "VALUE", parameters = { @Parameter("(?:(\\d+)ms)|(.*)") }, parser = RegexTextParser.class)
		void parseValue(String durationTxt, String message) {
			value = message;
			if (durationTxt != null) {
				duration = Integer.parseInt(durationTxt);
			}
		}

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

		public Number getDuration() {
			return duration;
		}

		public void setDuration(Number duration) {
			this.duration = duration;
		}

		@Override
		public String toString() {
			return String.format("LogDetail [name=%s, value=%s, duration=%s]",
					name, value, duration);
		}

	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	public LogDetail[] getDetails() {
		return details;
	}

	public void setDetails(LogDetail[] details) {
		this.details = details;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	@Override
	public String toString() {
		return String
				.format("SBSClientLog [dateTime=%s, logLevel=%s, loggerName=%s, details=%s]",
						dateTime, logLevel, loggerName,
						Arrays.toString(details));
	}

}
