package com.hp.it.perf.ac.app.hpsc.beans;

import java.io.Serializable;
import java.util.Arrays;
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
import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DelimsListTextParser;
import com.hp.it.perf.ac.load.parse.parsers.MultipleDelimsTextParser;
import com.hp.it.perf.ac.load.util.Timeable;

@Entity()
@Table(name = "PORTLET_ERROR_LOG")
@AcCommonDataEntity
@ParserPattern(value = "ERROR_LOG", parameters = {
		@Parameter("{DATE_TIME} | {TRANSACTION_ID} | \\{{ERROR_INFO}\\} | {CONTEXT_INFO}"),
		@Parameter("{DATE_TIME} | {TRANSACTION_ID} | \\{{ERROR_INFO}\\}") }, parser = MultipleDelimsTextParser.class)
public class PortletErrorLog implements Timeable, Serializable,
		LocationBasedBean {

	private static final long serialVersionUID = 1L;

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

	@ParserPattern(value = "ERROR_INFO", parameters = { @Parameter(AcTextParserConstant.MATCH_ANY) })
	@Column(name = "ERROR_INFO", columnDefinition = "text")
	private String errorInfo;

	@Column(name = "CONTEXT_INFO", columnDefinition = "text")
	private String contextInfos;

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

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	@ParserPattern(value = "CONTEXT_INFO", parameters = {
			@Parameter(","),
			@Parameter(name = AcTextParserConstant.KEY_FORMAT, value = AcTextParserConstant.MATCH_ANY) }, parser = DelimsListTextParser.class)
	public void setContextInfo(String[] contextInfo) {
		this.contextInfos = Arrays.toString(contextInfo);
	}

	public String getContextInfos() {
		return contextInfos;
	}

	public void setContextInfos(String contextInfos) {
		this.contextInfos = contextInfos;
	}

	@AcCommonDataField(value = Field.Name)
	public String getErrorSummary() {
		if (errorInfo != null) {
			String firstLine = errorInfo.split("\n")[0];
			return firstLine.substring(0, Math.min(160, firstLine.length()));
		} else {
			return "";
		}
	}

	@Override
	public String toString() {
		return String
				.format("PortletErrorLog [dateTime=%s, transactionId=%s, errorInfo=%s, contextInfos=%s]",
						dateTime, transactionId, errorInfo, contextInfos);
	}

}
