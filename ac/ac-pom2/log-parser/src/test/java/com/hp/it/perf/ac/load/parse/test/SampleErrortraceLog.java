package com.hp.it.perf.ac.load.parse.test;

import java.io.Serializable;
import java.util.Date;

import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;
import com.hp.it.perf.ac.load.parse.parsers.RegexTextParser;
import com.hp.it.perf.ac.load.util.Timeable;

@ParserPattern(value = "ERRORTRACE_LOG", parameters = { @Parameter("{DATE_TIME} | {TRANSACTION_ID} | {ERROR_MESSAGE}") }, parser = DelimsTextParser.class)
class SampleErrortraceLog implements Timeable, Serializable {

	private static final long serialVersionUID = 5569201661055340561L;

	private long acid;

	@ParserPattern("DATE_TIME")
	private Date dateTime;

	@ParserPattern("TRANSACTION_ID")
	private long transactionId;

	// Use this complex regex to separate with other log bean (when using auto
	// detect processor)
	@ParserPattern(value = "ERROR_MESSAGE", parameters = { @Parameter("(?ms)^([^:|{0-9]+(?:: )?.*)") }, parser = RegexTextParser.class)
	private String errorMessage;

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

	@Override
	public String toString() {
		return String
				.format("SampleErrortraceLog [dateTime=%s, transactionId=%s, errorMessage=%s]",
						dateTime, transactionId, errorMessage);
	}

}
