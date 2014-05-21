package com.hp.it.perf.ac.app.hpsc.beans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hp.it.perf.ac.app.hpsc.HpscContextField;
import com.hp.it.perf.ac.app.hpsc.HpscContextField.HpscContextConverter;
import com.hp.it.perf.ac.app.hpsc.HpscDictionary.HpscContextType;
import com.hp.it.perf.ac.common.model.support.AcCommonDataEntity;
import com.hp.it.perf.ac.common.model.support.AcCommonDataField;
import com.hp.it.perf.ac.common.model.support.AcCommonDataField.Field;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DelimsListTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;
import com.hp.it.perf.ac.load.util.Timeable;

@Entity()
@Table(name = "PORTLET_BUSINESS_LOG")
@AcCommonDataEntity
@ParserPattern(value = "BUSINESS_LOG", parameters = { @Parameter("{DATE_TIME} | {TRANSACTION_ID} | {:SESSION_ID} | {HPSC_DC_ID} | {PORTLET_NAME} | {PORTLET_METHOD} | {PORTLET_MODE} | {:PORTLET_WINDOW} | {USER_LOGIN} | {LOCALE} | {CONTEXT_INFO} |  | {STATUS} | {DURATION}") }, parser = DelimsTextParser.class)
public class PortletBusinessLog implements Serializable, Timeable,
		LocationBasedBean {

	private static final long serialVersionUID = -2815045383149957504L;

	@Id
	@Column(name = "ACID")
	@AcCommonDataField(Field.Identifier)
	private long acid;

	@AcCommonDataField(Field.Created)
	@ParserPattern("DATE_TIME")
	@Column(name = "DATE_TIME")
	private Date dateTime;

	@Column(name = "LOCATION", length = 100)
	private String location;

	@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
	@HpscContextField(HpscContextType.PortletTransactionId)
	@ParserPattern("TRANSACTION_ID")
	@Column(name = "TRANSACTION_ID", length = 20)
	private long transactionId;

	@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
	@HpscContextField(HpscContextType.PortletDiagnostic)
	@ParserPattern("HPSC_DC_ID")
	@Column(name = "HPSC_DC_ID", length = 50)
	private String hpscDiagnosticId;

	@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
	@HpscContextField(HpscContextType.PortletName)
	@Column(name = "PORTLET_NAME", length = 50)
	private String portletName;

	@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
	@HpscContextField(HpscContextType.Location)
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@ParserPattern("PORTLET_NAME")
	void parsePortletName(String str) {
		portletName = str.length() > 50 ? str.substring(0, 50) : str;
	}

	@ParserPattern("PORTLET_METHOD")
	@Column(name = "PORTLET_METHOD", length = 20)
	private String portletMethod;

	@ParserPattern("PORTLET_MODE")
	@Column(name = "PORTLET_MODE", length = 20)
	private String portletMode;

	@ParserPattern("USER_LOGIN")
	@Column(name = "USER_LOGIN", length = 100)
	private String userLogin;

	// LocalParser
	@ParserPattern("LOCALE")
	@Column(name = "LOCALE", length = 10)
	private String locale;

	@ParserPattern("STATUS")
	@Column(name = "STATUS", length = 10)
	@AcCommonDataField(value = Field.Level)
	private String status;

	@Column(name = "DURATION")
	@AcCommonDataField(value = Field.Duration)
	private int duration;

	@Column(name = "CONTEXT_INFOS", columnDefinition = "text")
	private String contextInfos;

	@Transient
	private static Pattern pattern = Pattern.compile("\\{(.*)\\}");

	@ParserPattern(value = "CONTEXT_INFO", parameters = { @Parameter(",") }, parser = DelimsListTextParser.class)
	public void parseContextInfo(String[] parameters) {
		for (int i = 0; i < parameters.length; i++) {
			Matcher matcher = pattern.matcher(parameters[i]);
			if (matcher.matches()) {
				parameters[i] = matcher.group(1);
			}
		}
		this.contextInfos = Arrays.toString(parameters);
		contextInfos = contextInfos.substring(0,
				Math.min(5000, contextInfos.length()));
	}

	@ParserPattern("DURATION")
	public void parseDuration(Number duration) {
		this.duration = (int) (duration.doubleValue() * 1000);
	}

	public long getAcid() {
		return acid;
	}

	public void setAcid(long acid) {
		this.acid = acid;
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

	@AcCommonDataField(value = Field.Name)
	public String getPortletNameMethod() {
		return portletName + "(" + portletMethod + ")";
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

	@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
	@HpscContextField(value = HpscContextType.UserLogin, ignoreEmpty = true)
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

	public String getContextInfos() {
		return contextInfos;
	}

	public void setContextInfos(String contextInfos) {
		this.contextInfos = contextInfos;
	}

	@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
	@HpscContextField(HpscContextType.DiagnosticID)
	public String getPortalDiagnosticId() {
		if (hpscDiagnosticId != null) {
			int seqIndex = hpscDiagnosticId.lastIndexOf('+');
			if (seqIndex > 0) {
				return hpscDiagnosticId.substring(0, seqIndex);
			}
		}
		return hpscDiagnosticId;
	}

	@Override
	public String toString() {
		return String
				.format("PortletBusinessLog [dateTime=%s, transactionId=%s, hpscDiagnosticId=%s, portletName=%s, portletMethod=%s, portletMode=%s, userLogin=%s, locale=%s, status=%s, duration=%s, contextInfos=%s]",
						dateTime, transactionId, hpscDiagnosticId, portletName,
						portletMethod, portletMode, userLogin, locale, status,
						duration, contextInfos);
	}

}
