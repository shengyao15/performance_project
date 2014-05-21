package com.hp.it.perf.ac.app.hpsc.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;

import com.hp.it.perf.ac.app.hpsc.HpscContextField;
import com.hp.it.perf.ac.app.hpsc.HpscContextField.HpscContextConverter;
import com.hp.it.perf.ac.app.hpsc.HpscDictionary.HpscContextType;
import com.hp.it.perf.ac.common.model.support.AcCommonDataField;
import com.hp.it.perf.ac.common.model.support.AcCommonDataField.Field;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.ConstantTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;
import com.hp.it.perf.ac.load.parse.parsers.MultipleDelimsTextParser;
import com.hp.it.perf.ac.load.parse.plugins.AcBeanParseHook;
import com.hp.it.perf.ac.load.util.Timeable;

@ParserPattern(value = "HP_JVM_GC_LOG", parameters = {
		@Parameter("<GC{GC_HEAD}: {ARG_NAME}={ARG_VALUE} >"),
		@Parameter("<GC: {GC_MAIN_TYPE} {GC_SUB_TYPE}  {JVM_TIME} {GC_SEQ} {TRIGGER_BYTES} {TENURE_LEVEL} {SURVIVOR_GEN} {NEW_GEN} {OLD_GEN} {PERM_GEN} {GC_STW_TIME} {GC_TIME} >") }, parser = MultipleDelimsTextParser.class)
public class HPJVMGCLog implements AcBeanParseHook, Timeable, LocationBasedBean {

	@ParserPattern(value = "GEN_SIZE", parameters = { @Parameter("{BEFORE} {AFTER} {CAPACITY}") }, parser = DelimsTextParser.class)
	public static class GenerationSize {

		@ParserPattern("BEFORE")
		public long before;

		@ParserPattern("AFTER")
		public long after;

		@ParserPattern("CAPACITY")
		public long capacity;

		@Override
		public String toString() {
			return String.format(
					"GenerationSize [before=%s, after=%s, capacity=%s]",
					before, after, capacity);
		}

	}

	private boolean head;

	@Column(name = "LOCATION", length = 100)
	private String location;

	@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
	@HpscContextField(HpscContextType.Location)
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@ParserPattern(value = "GC_HEAD", parameters = { @Parameter("H"),
			@Parameter("") }, parser = ConstantTextParser.class)
	void parseHead(String headString) {
		head = "H".equals(headString);
	}

	@ParserPattern("ARG_NAME")
	private String argName;

	@ParserPattern("ARG_VALUE")
	private String argValue;

	@ParserPattern("GC_MAIN_TYPE")
	private int gcMainType;

	@ParserPattern("GC_SUB_TYPE")
	private Number gcSubType;

	@ParserPattern("JVM_TIME")
	private Number jvmTime;

	private Date dateTime;

	@ParserPattern("GC_SEQ")
	private int gcSeq;

	@ParserPattern("TRIGGER_BYTES")
	private int triggerBytes;

	@ParserPattern("TENURE_LEVEL")
	private int tenureLevel;

	@ParserPattern("SURVIVOR_GEN")
	private GenerationSize survivorGen;

	@ParserPattern("NEW_GEN")
	private GenerationSize newGen;

	@ParserPattern("OLD_GEN")
	private GenerationSize oldGen;

	@ParserPattern("PERM_GEN")
	private GenerationSize permGen;

	@ParserPattern("GC_STW_TIME")
	private Number gcStdDuration;

	@ParserPattern("GC_TIME")
	private Number gcTotalDuration;

	public boolean isHead() {
		return head;
	}

	public String getArgName() {
		return argName;
	}

	public String getArgValue() {
		return argValue;
	}

	public int getGcMainType() {
		return gcMainType;
	}

	public Number getGcSubType() {
		return gcSubType;
	}

	public Number getJvmTime() {
		return jvmTime;
	}

	public int getGcSeq() {
		return gcSeq;
	}

	public int getTriggerBytes() {
		return triggerBytes;
	}

	public int getTenureLevel() {
		return tenureLevel;
	}

	public Number getGcStdDuration() {
		return gcStdDuration;
	}

	public Number getGcTotalDuration() {
		return gcTotalDuration;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public GenerationSize getSurvivorGen() {
		return survivorGen;
	}

	public GenerationSize getNewGen() {
		return newGen;
	}

	public GenerationSize getOldGen() {
		return oldGen;
	}

	public GenerationSize getPermGen() {
		return permGen;
	}

	@Override
	public String toString() {
		return String
				.format("HPJVMGCLog [head=%s, argName=%s, argValue=%s, gcMainType=%s, gcSubType=%s, jvmTime=%s, dateTime=%s, gcSeq=%s, triggerBytes=%s, tenureLevel=%s, survivorGen=%s, newGen=%s, oldGen=%s, permGen=%s, gcStdDuration=%s, gcTotalDuration=%s]",
						head, argName, argValue, gcMainType, gcSubType,
						jvmTime, dateTime, gcSeq, triggerBytes, tenureLevel,
						survivorGen, newGen, oldGen, permGen, gcStdDuration,
						gcTotalDuration);
	}

	@Override
	public void onReady(AcContentLine contentLine, AcTextParserContext context) {
		Date startDate;
		if (isHead() && "starttime".equals(getArgName())) {
			// "Wed May 30 01:52:27 UTC 2012"
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"\"EEE MMM dd HH:mm:ss z yyyy\"", Locale.US);
				startDate = dateFormat.parse(getArgValue());
				context.setAttribute("gc-start-time", startDate);
			} catch (ParseException e) {
				throw new IllegalArgumentException(e);
			}
		}
		startDate = (Date) context.getAttribute("gc-start-time");
		if (!isHead() && startDate != null) {
			setDateTime(new Date(startDate.getTime()
					+ ((long) getJvmTime().doubleValue() * 1000)));
		}
	}
}
