package com.hp.it.perf.ac.app.hpsc.beans;

import static com.hp.it.perf.ac.common.data.AcDataUtils.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

import com.hp.it.perf.ac.common.data.AcData;
import com.hp.it.perf.ac.common.data.AcDataBean;
import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataOutput;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.parsers.DateTimeTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsListTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;
import com.hp.it.perf.ac.load.parse.parsers.RegexTextParser;
import com.hp.it.perf.ac.load.parse.plugins.AcBeanParseHook;
import com.hp.it.perf.ac.load.util.Timeable;

@ParserPattern(value = "SPF_PERFORMANCE_LOG", parameters = { @Parameter(value = "{DATE_TIME} [{THREAD}] [{HPSC_DC_ID}] - <{DETAIL_LIST}>") }, parser = DelimsTextParser.class)
@AcDataBean
public class SPFPerformanceLog implements AcData, Serializable, Timeable,
		AcBeanParseHook, LocationBasedBean {

	private static final long serialVersionUID = 6768121186542789513L;

	@ParserPattern(value = "DATE_TIME", parser = DateTimeTextParser.class)
	private Date dateTime;

	private String location;

	@ParserPattern("HPSC_DC_ID")
	private String hpscDiagnosticId;

	@ParserPattern("THREAD")
	private String threadName;

	private static Pattern dcidPattern = Pattern
			.compile("^\\w+\\+[^\\(\\)'\":\\[\\]]+$");

	@ParserPattern(value = "DETAIL_LIST", parameters = {
			@Parameter(name = DelimsListTextParser.DELIMS, value = ","),
			@Parameter(name = AcTextParserConstant.KEY_PATTERN, value = "DETAIL") }, parser = DelimsListTextParser.class)
	private Detail[] detailList;

	@ParserPattern(value = "DETAIL", parameters = {
			@Parameter("^(WSRP_CALL|PROFILE_CALL|GROUPS_CALL|REQUEST)(?:\\[(.*)\\])??:(OK|ERROR|unknown)(?ms:\\[(.*)\\])??=(-?\\d+)$"),
			@Parameter(name = AcTextParserConstant.KEY_NAME, value = "TYPE"),
			@Parameter(name = AcTextParserConstant.KEY_NAME, value = "NAME"),
			@Parameter(name = AcTextParserConstant.KEY_NAME, value = "STATUS"),
			@Parameter(name = AcTextParserConstant.KEY_NAME, value = "STATUS_DETAIL"),
			@Parameter(name = AcTextParserConstant.KEY_NAME, value = "DURATION") }, parser = RegexTextParser.class)
	@AcDataBean
	public static class Detail implements AcData, Serializable {

		private static final long serialVersionUID = -3288218866215945563L;

		private String name;

		private String status;

		private boolean success;
		
		@ParserPattern("NAME")
		void parseDetailName(String str) {
			name = str.length() > 255 ? str.substring(0, 255) : str;
		}

		@ParserPattern("STATUS")
		public void parseStatus(String status) {
			this.status = status;
			this.success = "OK".equals(status);
		}

		@ParserPattern("STATUS_DETAIL")
		private String statusDetail;

		@ParserPattern("DURATION")
		private int duration;

		public static enum Type {
			REQUEST, WSRP_CALL, PROFILE_CALL, GROUPS_CALL;
		}

		@ParserPattern("TYPE")
		private Type type;

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
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

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public String getStatusDetail() {
			return statusDetail;
		}

		public void setStatusDetail(String statusDetail) {
			this.statusDetail = statusDetail;
		}

		@Override
		public String toString() {
			return String
					.format("Detail [name=%s, status=%s, success=%s, statusDetail=%s, duration=%s, type=%s]",
							name, status, success, statusDetail, duration, type);
		}

		public void toOutput(AcDataOutput out) throws IOException {
			out.writeString(getName());
			out.writeString(getStatus());
			out.writeString(getStatusDetail());
			out.writeInt(getDuration());
			out.writeInt(getType().ordinal());
		}

		public void fromInput(AcDataInput in) throws IOException {
			setName(in.readString());
			setStatus(in.readString());
			setStatusDetail(in.readString());
			setDuration(in.readInt());
			setType(Type.values()[in.readInt()]);
		}

	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getHpscDiagnosticId() {
		return hpscDiagnosticId;
	}

	public void setHpscDiagnosticId(String hpscDiagnosticId) {
		this.hpscDiagnosticId = hpscDiagnosticId;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public Detail[] getDetailList() {
		return detailList;
	}

	public void setDetailList(Detail[] detailList) {
		this.detailList = detailList;
	}

	@Override
	public String toString() {
		return String
				.format("SPFPerformanceLog [dateTime=%s, hpscDiagnosticId=%s, threadName=%s, detailList=%s]",
						dateTime, hpscDiagnosticId, threadName,
						Arrays.toString(detailList));
	}

	public void toOutput(AcDataOutput out) throws IOException {
		out.writeInt(getDetailList().length);
		for (Detail detail : getDetailList()) {
			detail.toOutput(out);
		}
		out.writeLong(getDateTime().getTime());
		out.writeString(getHpscDiagnosticId());
		out.writeString(getThreadName());
	}

	public void fromInput(AcDataInput in) throws IOException {
		Detail[] theDetails = new Detail[readVInt(in)];
		for (int i = 0; i < theDetails.length; i++) {
			Detail detail = new Detail();
			detail.fromInput(in);
			theDetails[i] = detail;
		}
		setDetailList(theDetails);
		setDateTime(new Date(in.readLong()));
		setHpscDiagnosticId(in.readString());
		setThreadName(in.readString());
	}

	@Override
	public void onReady(AcContentLine contentLine, AcTextParserContext context) {
		// check diagnostic id and thread name
		Boolean reverse = (Boolean) context.getAttribute(dcidPattern);
		if (reverse == null) {
			reverse = !dcidPattern.matcher(hpscDiagnosticId).matches();
			context.setAttribute(dcidPattern, reverse);
		}
		if (reverse) {
			String tmp = hpscDiagnosticId;
			hpscDiagnosticId = threadName;
			threadName = tmp;
		}
	}

}
