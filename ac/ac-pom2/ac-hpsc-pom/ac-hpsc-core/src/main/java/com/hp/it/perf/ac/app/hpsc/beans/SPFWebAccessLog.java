package com.hp.it.perf.ac.app.hpsc.beans;

import java.util.Date;

import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;
import com.hp.it.perf.ac.load.parse.plugins.AcBeanParseHook;

//%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\" %D %{SPF_DC_SID}C+%{SPF_DC_ID}i
// Refer to http://httpd.apache.org/docs/2.0/mod/mod_log_config.html#formats
@ParserPattern(value = "SPF_WEB_ACCESS_LOG", parameters = { @Parameter(value = "{REMOTE_HOST} {:REMOTE_LOGNAME} {:REMOTE_USER} [{REV_DATE_TIME}] \"{REQUEST_METHOD} {REQUEST_LINE} HTTP/{HTTP_VERSION}\" {HTTP_STATUS} {RESPONSE_SIZE} \"{REFERER}\" \"{USER_AGENT}\" {TIME_TAKEN} {SPF_DC_SID}+{SPF_DC_ID}") }, parser = DelimsTextParser.class)
public class SPFWebAccessLog implements AcBeanParseHook {

	@ParserPattern("REMOTE_HOST")
	private String remoteHost;

	@ParserPattern(value = "REV_DATE_TIME", parameters = { @Parameter("dd/MMM/yyyy:HH:mm:ss Z") })
	private Date dateTime;

	@ParserPattern("REQUEST_METHOD")
	private String requestMethod;

	@ParserPattern("REQUEST_LINE")
	private String requestPath;

	@ParserPattern("HTTP_VERSION")
	private String httpVersion;

	@ParserPattern("HTTP_STATUS")
	private int httpStatus;

	private int responseSize;

	@ParserPattern("RESPONSE_SIZE")
	void parseResponseSize(String size) {
		if ("-".equals(size)) {
			responseSize = 0;
		} else {
			responseSize = Integer.parseInt(size);
		}
	}

	@ParserPattern("REFERER")
	private String referer;

	@ParserPattern("USER_AGENT")
	private String userAgent;

	@ParserPattern("TIME_TAKEN")
	private int duration;

	@ParserPattern("SPF_DC_SID")
	private String spfDcSid;

	@ParserPattern("SPF_DC_ID")
	private String spfDcId;

	@Override
	public void onReady(AcContentLine contentLine, AcTextParserContext context) {
		referer = normalize(referer);
		userAgent = normalize(userAgent);
		spfDcSid = normalize(spfDcSid);
		spfDcId = normalize(spfDcId);
	}

	private String normalize(String text) {
		if ("-".equals(text)) {
			return "";
		} else {
			return text;
		}
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}

	public int getResponseSize() {
		return responseSize;
	}

	public void setResponseSize(int responseSize) {
		this.responseSize = responseSize;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getSpfDcSid() {
		return spfDcSid;
	}

	public void setSpfDcSid(String spfDcSid) {
		this.spfDcSid = spfDcSid;
	}

	public String getSpfDcId() {
		return spfDcId;
	}

	public void setSpfDcId(String spfDcId) {
		this.spfDcId = spfDcId;
	}

}
