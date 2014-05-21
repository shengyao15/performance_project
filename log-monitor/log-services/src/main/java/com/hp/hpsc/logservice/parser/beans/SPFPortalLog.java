package com.hp.hpsc.logservice.parser.beans;

import java.util.Date;

import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DateTimeTextParser;
import com.hp.it.perf.ac.load.parse.parsers.MultipleDelimsTextParser;
import com.hp.it.perf.ac.load.parse.parsers.RegexTextParser;
import com.hp.it.perf.ac.load.util.Timeable;

@ParserPattern(value = "SPF_PORTAL_LOG", parameters = {
		@Parameter("{DATE_TIME} [{THREAD}] {LOG_LEVEL} {LOG_NAME} - [{SITE}] [{HPSC_DC_ID}] - {MESSAGE}"),
		@Parameter("{DATE_TIME}[{THREAD}]{LOG_LEVEL}{LOG_NAME}-[{SITE}][{HPSC_DC_ID}]- {MESSAGE}") }, parser = MultipleDelimsTextParser.class)
public class SPFPortalLog implements Timeable {

	@ParserPattern(value = "DATE_TIME", parameters = { @Parameter("yyyy-MM-dd HH:mm:ss,SSS") }, parser = DateTimeTextParser.class)
	private Date dateTime;

	@ParserPattern(value = "THREAD", parameters = { @Parameter(AcTextParserConstant.MATCH_ANY) })
	private String threadName;

	public enum LogLevel {
		DEBUG, INFO, WARN, ERROR;
	}

	@ParserPattern("LOG_LEVEL")
	private LogLevel logLevel;

	@ParserPattern("LOG_NAME")
	private String logName;

	@ParserPattern("SITE")
	private String site;

	@ParserPattern("HPSC_DC_ID")
	private String hpscDiagnosticId;

	@ParserPattern(value = "MESSAGE", parameters = { @Parameter("(?ms)(?:The portlet with title, (.+?), and UID, (.+?), failed to render.\\n?(.*))|(.*)") }, parser = RegexTextParser.class)
	void parseMessage(String portletTitle, String portletUID, String errorMsg,
			String generalMsg) {
		if (portletTitle != null) {
			renderError = new PortletRenderError();
			renderError.portletTitle = portletTitle;
			renderError.portletUID = portletUID;
			renderError.errorMessage = errorMsg;
		} else {
			message = generalMsg;
		}
	}

	private String message;

	private PortletRenderError renderError;

	public static class PortletRenderError {

		private String portletTitle;

		private String portletUID;

		private String errorMessage;

		@Override
		public String toString() {
			return String
					.format("PortletRenderError [portletTitle=%s, portletUID=%s, errorMessage=%s]",
							portletTitle, portletUID, errorMessage);
		}

		public String getPortletTitle() {
			return portletTitle;
		}

		public void setPortletTitle(String portletTitle) {
			this.portletTitle = portletTitle;
		}

		public String getPortletUID() {
			return portletUID;
		}

		public void setPortletUID(String portletUID) {
			this.portletUID = portletUID;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}

	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
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

	public PortletRenderError getRenderError() {
		return renderError;
	}

	public void setRenderError(PortletRenderError renderError) {
		this.renderError = renderError;
	}

	@Override
	public String toString() {
		return String
				.format("SPFPortalLog [dateTime=%s, threadName=%s, logLevel=%s, logName=%s, site=%s, hpscDiagnosticId=%s, message=%s, renderError=%s]",
						dateTime, threadName, logLevel, logName, site,
						hpscDiagnosticId, message, renderError);
	}

}
