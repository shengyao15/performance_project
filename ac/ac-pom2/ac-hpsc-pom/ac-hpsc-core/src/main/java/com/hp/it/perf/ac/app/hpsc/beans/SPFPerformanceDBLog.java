package com.hp.it.perf.ac.app.hpsc.beans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import com.hp.it.perf.ac.load.util.Timeable;

@Entity
@Table(name = "SPF_PERFORMANCE_LOG")
@AcCommonDataEntity
public class SPFPerformanceDBLog implements Timeable, Serializable {

	private static final long serialVersionUID = 2004816764285308313L;

	@Id
	@Column(name = "ACID")
	@AcCommonDataField(Field.Identifier)
	private long acid;

	@Column(name = "DATE_TIME")
	@AcCommonDataField(Field.Created)
	private Date dateTime;
	
	@Column(name = "LOCATION", length = 100)
	private String location;

	@Column(name = "HPSC_DC_ID", length = 50)
	@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
	@HpscContextField(HpscContextType.DiagnosticID)
	private String hpscDiagnosticId;

	@Column(name = "THREAD_NAME", length = 100)
	private String threadName;

	@Column(name = "NAME", length = 255)
	@AcCommonDataField(Field.Name)
	private String name;

	@Column(name = "LOG_STATUS", length = 20)
	@AcCommonDataField(Field.Level)
	private String status;

	@Column(name = "STATUS_DETAIL", columnDefinition = "text")
	private String statusDetail;

	@Column(name = "DURATION")
	@AcCommonDataField(Field.Duration)
	private int duration;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = Detail.class)
	@JoinColumn(name = "PARENT_ACID")
	@OrderColumn(name = "DETAIL_ORDER")
	private Detail[] details;

	@Entity
	@Table(name = "SPF_PERFORMANCE_LOG_DETAIL")
	@AcCommonDataEntity
	public static class Detail implements Serializable {

		private static final long serialVersionUID = 2746590599388101764L;

		@Id
		@Column(name = "ACID")
		@AcCommonDataField(Field.Identifier)
		private long acid;

		@Column(name = "TYPE", length = 20)
		@AcCommonDataField(Field.Type)
		private String type;

		@Column(name = "NAME", length = 50)
		@AcCommonDataField(Field.Name)
		private String name;

		@Column(name = "LOG_STATUS", length = 20)
		@AcCommonDataField(Field.Level)
		private String status;

		@Column(name = "STATUS_DETAIL", columnDefinition = "text")
		private String statusDetail;

		@Column(name = "DURATION")
		@AcCommonDataField(Field.Duration)
		private int duration;

		@Column(name = "HPSC_DC_ID", length = 50)
		@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
		@HpscContextField(HpscContextType.DiagnosticID)
		private String hpscDiagnosticId;

		public long getAcid() {
			return acid;
		}

		public void setAcid(long acid) {
			this.acid = acid;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
		@HpscContextField(HpscContextType.PortletName)
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
		@HpscContextField(value = HpscContextType.ErrorDetail, ignoreEmpty = true)
		public String getErrorDetail() {
			if ("ERROR".equals(getStatus())){
				return getStatusDetail();
			} else {
				return null;
			}
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getStatusDetail() {
			return statusDetail;
		}

		public void setStatusDetail(String statusDetail) {
			this.statusDetail = statusDetail;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

		public String getHpscDiagnosticId() {
			return hpscDiagnosticId;
		}

		public void setHpscDiagnosticId(String hpscDiagnosticId) {
			this.hpscDiagnosticId = hpscDiagnosticId;
		}
		
		@Override
		public String toString() {
			return String
					.format("Detail [name=%s, hpscDiagnosticId=%s, status=%s, statusDetail=%s, duration=%s, type=%s]",
							name, hpscDiagnosticId, status, statusDetail, duration, type);
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

	@AcCommonDataField(value = Field.Context, converter = HpscContextConverter.class)
	@HpscContextField(value = HpscContextType.NoWSRP, ignoreEmpty = true)
	public String getNoWSRP() {
		if (details.length == 0) {
			return "Y";
		} else {
			return null;
		}
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

	public String getStatusDetail() {
		return statusDetail;
	}

	public void setStatusDetail(String statusDetail) {
		this.statusDetail = statusDetail;
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

	@AcCommonDataField(Field.Type)
	String commonDataType() {
		return "REQUEST";
	}
	
	@Override
	public String toString() {
		return String
				.format("SPFPerformanceLog [name=%s, dateTime=%s, hpscDiagnosticId=%s, threadName=%s, detailList=%s]",
						name, dateTime, hpscDiagnosticId, threadName,
						Arrays.toString(details));
	}

}
