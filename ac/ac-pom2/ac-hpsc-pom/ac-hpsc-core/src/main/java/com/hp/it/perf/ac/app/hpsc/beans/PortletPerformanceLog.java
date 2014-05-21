package com.hp.it.perf.ac.app.hpsc.beans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.hp.it.perf.ac.app.hpsc.HpscContextField;
import com.hp.it.perf.ac.app.hpsc.HpscContextField.HpscContextConverter;
import com.hp.it.perf.ac.app.hpsc.HpscDictionary.HpscContextType;
import com.hp.it.perf.ac.common.model.support.AcCommonDataEntity;
import com.hp.it.perf.ac.common.model.support.AcCommonDataField;
import com.hp.it.perf.ac.common.model.support.AcCommonDataField.Field;
import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DelimsListTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;
import com.hp.it.perf.ac.load.parse.parsers.NumberTextParser;
import com.hp.it.perf.ac.load.util.Timeable;

@Entity()
@Table(name = "PORTLET_PERFORMANCE_LOG")
@AcCommonDataEntity
@ParserPattern(value = "PERFORMANCE_LOG", parameters = { @Parameter("{DATE_TIME} | {TRANSACTION_ID} | {SESSION_ID} | {PORTLET_NAME} | {PORTLET_METHOD} | {DURATION} | {PERFORMANCE_DETAIL_LIST}") }, parser = DelimsTextParser.class)
public class PortletPerformanceLog implements Timeable, Serializable,
		LocationBasedBean {

	private static final long serialVersionUID = -2367510455532557647L;

	@Id
	@Column(name = "ACID")
	@AcCommonDataField(Field.Identifier)
	private long acid;

	@ParserPattern("DATE_TIME")
	@Column(name = "DATE_TIME")
	@AcCommonDataField(Field.Created)
	private Date dateTime;

	@Column(name = "LOCATION", length = 100)
	private String location;
	
	@ParserPattern("TRANSACTION_ID")
	@Column(name = "TRANSACTION_ID")
	@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
	@HpscContextField(HpscContextType.PortletTransactionId)
	private long transactionId;

	@ParserPattern("SESSION_ID")
	@Column(name = "SESSION_ID")
	private long sessionId;

	@Column(name = "PORTLET_NAME", length = 50)
	@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
	@HpscContextField(HpscContextType.PortletName)
	private String portletName;

	@ParserPattern("PORTLET_NAME")
	void parsePortletName(String str) {
		// fix incorrect log format
		portletName = str.length() > 50 ? str.substring(0, 50) : str;
	}

	@ParserPattern("PORTLET_METHOD")
	@Column(name = "PORTLET_METHOD", length = 20)
	private String portletMethod;

	@Column(name = "DURATION")
	@AcCommonDataField(value = Field.Duration)
	private int duration;

	@ParserPattern(value = "PERFORMANCE_DETAIL_LIST", parameters = {
			@Parameter(name = DelimsListTextParser.DELIMS, value = ","),
			@Parameter(name = AcTextParserConstant.KEY_PATTERN, value = "PERFORMANCE_DETAIL") }, parser = DelimsListTextParser.class)
//	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = Detail.class)
//	@JoinColumn(name = "PARENT_ACID")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "PORTLET_PERFORMANCE_LOG_DETAIL", joinColumns = @JoinColumn(name = "ACID"))
	@OrderColumn(name = "DETAIL_ORDER")
	//@AcCommonDataField(value = Field.Related)
	private Detail[] details;

	@ParserPattern(value = "PERFORMANCE_DETAIL", parameters = { @Parameter("\\{{NAME}:{DURATION}\\}") }, parser = DelimsTextParser.class)
//	@Entity()
//	@Table(name = "PORTLET_PERFORMANCE_LOG_DETAIL")
	@Embeddable
	//@AcCommonDataEntity
	public static class Detail implements Serializable {

		private static final long serialVersionUID = 4413253812828204650L;

		@ParserPattern("NAME")
		@Column(name = "NAME", length = 255)
		//@AcCommonDataField(Field.Name)
		private String name;

		@Column(name = "DURATION")
		//@AcCommonDataField(Field.Duration)
		private int duration;

		@ParserPattern(value = "DURATION", parser = NumberTextParser.class)
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

		//@AcCommonDataField(Field.Type)
		public String getType() {
			if (name.startsWith("Controller")) {
				return "Controller";
			}
			if (name.startsWith("View")) {
				return "View";
			}
			if (name.startsWith("Service")) {
				return "Service";
			}
			if (name.startsWith("Backend")) {
				return "Backend";
			}
			return "Other";
		}

		@Override
		public String toString() {
			return String.format("Detail [name=%s, duration=%s]", name,
					duration);
		}
	}

	@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
	@HpscContextField(HpscContextType.Location)
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@ParserPattern(value = "DURATION", parser = NumberTextParser.class)
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

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	@AcCommonDataField(Field.Name)
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
