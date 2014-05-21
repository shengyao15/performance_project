package com.hp.it.perf.ac.load.parse.test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DelimsListTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;
import com.hp.it.perf.ac.load.util.Timeable;

@ParserPattern(value = "ERROR_LOG", parameters = { @Parameter("{DATE_TIME} | {TRANSACTION_ID} | \\{{ERROR_INFO}\\} | {CONTEXT_INFO}") }, parser = DelimsTextParser.class)
class SampleErrorLog implements Timeable, Serializable {

	private static final long serialVersionUID = 1L;

	private long acid;

	@ParserPattern("DATE_TIME")
	private Date dateTime;

	@ParserPattern("TRANSACTION_ID")
	private long transactionId;

	@ParserPattern(value = "ERROR_INFO", parameters = { @Parameter(AcTextParserConstant.MATCH_ANY) })
	private String errorInfo;

	private String contextInfos;

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

	@Override
	public String toString() {
		return String
				.format("SampleErrorLog [dateTime=%s, transactionId=%s, errorInfo=%s, contextInfos=%s]",
						dateTime, transactionId, errorInfo,
						contextInfos);
	}

}
