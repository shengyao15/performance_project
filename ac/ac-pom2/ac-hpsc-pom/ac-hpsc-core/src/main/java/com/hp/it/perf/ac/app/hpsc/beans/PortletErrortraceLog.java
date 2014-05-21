package com.hp.it.perf.ac.app.hpsc.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hp.it.perf.ac.app.hpsc.HpscContextField;
import com.hp.it.perf.ac.app.hpsc.HpscContextField.HpscContextConverter;
import com.hp.it.perf.ac.app.hpsc.HpscDictionary.HpscContextType;
import com.hp.it.perf.ac.common.model.support.AcCommonDataEntity;
import com.hp.it.perf.ac.common.model.support.AcCommonDataField;
import com.hp.it.perf.ac.common.model.support.AcCommonDataField.Field;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;
import com.hp.it.perf.ac.load.parse.parsers.RegexTextParser;
import com.hp.it.perf.ac.load.util.Timeable;

@Entity()
@Table(name = "PORTLET_ERRORTRACE_LOG")
@AcCommonDataEntity
@ParserPattern(value = "ERRORTRACE_LOG", parameters = { @Parameter("{DATE_TIME} | {TRANSACTION_ID} | {ERROR_MESSAGE}") }, parser = DelimsTextParser.class)
public class PortletErrortraceLog implements Timeable, Serializable,
		LocationBasedBean {

	private static final long serialVersionUID = 5569201661055340561L;

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
	@Column(name = "TRANSACTION_ID", length = 20)
	@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
	@HpscContextField(HpscContextType.PortletTransactionId)
	private long transactionId;

	// Use this complex regex to separate with other log bean (when using auto
	// detect processor)
	@ParserPattern(value = "ERROR_MESSAGE", parameters = { @Parameter("(?ms)^([^:|{0-9]+(?:: )?.*)") }, parser = RegexTextParser.class)
	@Column(name = "ERROR_MESSAGE", columnDefinition = "text")
	private String errorMessage;

	@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
	@HpscContextField(HpscContextType.Location)
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@AcCommonDataField(value = Field.Name)
	public String getErrorSummary() {
		if (errorMessage != null) {
			String firstLine = errorMessage.split("\n")[0];
			return firstLine.substring(0, Math.min(160, firstLine.length()));
		} else {
			return "";
		}
	}

	@Override
	public String toString() {
		return String
				.format("PortletErrortraceLog [dateTime=%s, transactionId=%s, errorMessage=%s]",
						dateTime, transactionId, errorMessage);
	}

}
