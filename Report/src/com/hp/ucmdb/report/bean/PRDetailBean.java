/**
 * Copyright : HP
 * project : Report
 * Create by : Steven Zhang(Ling Kai)
 * Create on : 2011-7-14
 */
package com.hp.ucmdb.report.bean;

public class PRDetailBean {
     private int recordId;
	 private int fileId;
	 private int recordSeqNo;
	 private String name;
	 private String extId;
	 private String orgBrand;
	 private String orgModel;
	 private String prBrand;
	 private String prModel;
	 private String recordStatus;
	public int getRecordId() {
		return recordId;
	}
	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}
	public int getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	public int getRecordSeqNo() {
		return recordSeqNo;
	}
	public void setRecordSeqNo(int recordSeqNo) {
		this.recordSeqNo = recordSeqNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExtId() {
		return extId;
	}
	public void setExtId(String extId) {
		this.extId = extId;
	}
	public String getOrgBrand() {
		return orgBrand;
	}
	public void setOrgBrand(String orgBrand) {
		this.orgBrand = orgBrand;
	}
	public String getOrgModel() {
		return orgModel;
	}
	public void setOrgModel(String orgModel) {
		this.orgModel = orgModel;
	}
	public String getPrBrand() {
		return prBrand;
	}
	public void setPrBrand(String prBrand) {
		this.prBrand = prBrand;
	}
	public String getPrModel() {
		return prModel;
	}
	public void setPrModel(String prModel) {
		this.prModel = prModel;
	}
	public String getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	 
	 
}
