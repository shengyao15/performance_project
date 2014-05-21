package com.hp.it.perf.ac.app.hpsc.beans;

import java.util.Date;

import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;
import com.hp.it.perf.ac.load.parse.parsers.EnumTextParser;
import com.hp.it.perf.ac.load.util.Timeable;

@ParserPattern(value = "OPEN_PORTAL_LOG", parameters = { @Parameter("{DATE_TIME} [{HPSC_DC_ID}] {LOG_LEVEL} {MESSAGE}") }, parser = DelimsTextParser.class)
public class OpenPortalLog implements Timeable {

	@ParserPattern("DATE_TIME")
	private Date dateTime;

	@ParserPattern("HPSC_DC_ID")
	private String hpscDiagnosticId;

	public enum LogLevel {
		FINEST, FINER, FINE, CONFIG, INFO, WARNING, SEVERE;
	}

	@ParserPattern(value = "LOG_LEVEL", parameters = { @Parameter(classValue = LogLevel.class) }, parser = EnumTextParser.class)
	private LogLevel logLevel;

	@ParserPattern(value = "MESSAGE", parameters = { @Parameter(AcTextParserConstant.MATCH_ANY) })
	private String message;

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	public String getHpscDiagnosticId() {
		return hpscDiagnosticId;
	}

	public void setHpscDiagnosticId(String hpscDiagnosticId) {
		this.hpscDiagnosticId = hpscDiagnosticId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return String
				.format("OpenPortalLog [dateTime=%s, hpscDiagnosticId=%s, logLevel=%s, message=%s]",
						dateTime, hpscDiagnosticId, logLevel, message);
	}

}
