/**
 * Copyright : HP
 * project : Report
 * Create by : Steven Zhang(Ling Kai)
 * Create on : 2011-7-11
 */
package com.hp.ucmdb.report.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterSummaryBean{
	private int fileId = 0;
	private String fileName = "";
	private String fileDataSource = "";
	private Date processStartDate = null;
	private Date processEndDate = null;
	private Integer recordCount = 0;
	private String fileStatus = "";
	private Integer recordSuccess = 0;
	private Integer recordsinError = 0;
	private boolean haveErrorRecords = false;
	public List<AdapterDetailBean> adapterDetailList = new ArrayList<AdapterDetailBean>();
	private String dsContactEmail;
	
	public int getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileDataSource() {
		return fileDataSource;
	}
	public void setFileDataSource(String fileDataSource) {
		this.fileDataSource = fileDataSource;
	}
	public Date getProcessStartDate() {
		return processStartDate;
	}
	public void setProcessStartDate(Date processStartDate) {
		this.processStartDate = processStartDate;
	}
	public Date getProcessEndDate() {
		return processEndDate;
	}
	public void setProcessEndDate(Date processEndDate) {
		this.processEndDate = processEndDate;
	}
	public Integer getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
	}
	public String getFileStatus() {
		return fileStatus;
	}
	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}
	public Integer getRecordSuccess() {
		return recordSuccess;
	}
	public void setRecordSuccess(Integer recordSuccess) {
		this.recordSuccess = recordSuccess;
	}
	public Integer getRecordsinError() {
		return recordsinError;
	}
	public void setRecordsinError(Integer recordsinError) {
		this.recordsinError = recordsinError;
	}
	public boolean isHaveErrorRecords() {
		return haveErrorRecords;
	}
	public void setHaveErrorRecords(boolean haveErrorRecords) {
		this.haveErrorRecords = haveErrorRecords;
	}
	public List<AdapterDetailBean> getAdapterDetailList() {
		return adapterDetailList;
	}
	public void setAdapterDetailList(List<AdapterDetailBean> adapterDetailList) {
		this.adapterDetailList = adapterDetailList;
	}
	public String getDsContactEmail() {
		return dsContactEmail;
	}
	public void setDsContactEmail(String dsContactEmail) {
		this.dsContactEmail = dsContactEmail;
	}
	
}
