package com.hp.hpsc.logservice.parser.beans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DelimsListTextParser;
import com.hp.it.perf.ac.load.parse.parsers.MultipleDelimsTextParser;

@ParserPattern(value = "ERROR_LOG", parameters = {
		@Parameter("{DATE_TIME} | {TRANSACTION_ID} | \\{{ERROR_INFO}\\} | {CONTEXT_INFO}"),
		@Parameter("{DATE_TIME} | {TRANSACTION_ID} | \\{{ERROR_INFO}\\}") }, parser = MultipleDelimsTextParser.class)
public class PortletErrorLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private long acid;

	@ParserPattern("DATE_TIME")
	private Date dateTime;
	
	private String location;

	@ParserPattern("TRANSACTION_ID")
	private long transactionId;

	@ParserPattern(value = "ERROR_INFO", parameters = { @Parameter(AcTextParserConstant.MATCH_ANY) })
	private String errorInfo;

	private String contextInfos;

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
