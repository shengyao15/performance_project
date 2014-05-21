package com.hp.it.perf.ac.load.parse.test;

import java.util.Arrays;
import java.util.Date;

import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DelimsListTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;

@ParserPattern(value = "BUSINESS_LOG", parameters = { @Parameter("{DATE_TIME} | {TRANSACTION_ID} | {:SESSION_ID} | {HPSC_DC_ID} | {PORTLET_NAME} | {PORTLET_METHOD} | {PORTLET_MODE} | {:PORTLET_WINDOW} | {USER_LOGIN} | {LOCALE} | {CONTEXT_INFO} |  | {STATUS} | {DURATION}") }, parser = DelimsTextParser.class)
class SampleBusinessLog implements Timeable {

	@ParserPattern("DATE_TIME")
	private Date dateTime;

	@ParserPattern("TRANSACTION_ID")
	private long transactionId;

	@ParserPattern("HPSC_DC_ID")
	private String hpscDiagnosticId;

	@ParserPattern("PORTLET_NAME")
	private String portletName;

	@ParserPattern("PORTLET_METHOD")
	private String portletMethod;

	@ParserPattern("PORTLET_MODE")
	private String portletMode;

	@ParserPattern("USER_LOGIN")
	private String userLogin;

	// LocalParser
	@ParserPattern("LOCALE")
	private String locale;

	@ParserPattern("STATUS")
	private String status;

	private int duration;

	@ParserPattern(value = "CONTEXT_INFO", parameters = { @Parameter(",") }, parser = DelimsListTextParser.class)
	private String[] contextInfo;

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

	public String getHpscDiagnosticId() {
		return hpscDiagnosticId;
	}

	public void setHpscDiagnosticId(String hpscDiagnosticId) {
		this.hpscDiagnosticId = hpscDiagnosticId;
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

	public String getPortletMode() {
		return portletMode;
	}

	public void setPortletMode(String portletMode) {
		this.portletMode = portletMode;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
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

	public String[] getContextInfo() {
		return contextInfo;
	}

	public void setContextInfo(String[] contextInfo) {
		this.contextInfo = contextInfo;
	}

	@Override
	public String toString() {
		return String
				.format("PortletBusinessLog [dateTime=%s, transactionId=%s, hpscDiagnosticId=%s, portletName=%s, portletMethod=%s, portletMode=%s, userLogin=%s, locale=%s, status=%s, duration=%s, contextInfo=%s]",
						dateTime, transactionId, hpscDiagnosticId, portletName,
						portletMethod, portletMode, userLogin, locale, status,
						duration, Arrays.toString(contextInfo));
	}

}
