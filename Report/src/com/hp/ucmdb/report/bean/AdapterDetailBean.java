/**
 * Copyright : HP
 * project : Report
 * Create by : Steven Zhang(Ling Kai)
 * Create on : 2011-7-18
 */
package com.hp.ucmdb.report.bean;

public class AdapterDetailBean {
    private int errorId;
    private int fileId;
	private int recordSeqNo;
	private String attributeName;
	private String attributeValue;
	private String errorNbr;
	private String errorMessage;
	public int getErrorId() {
		return errorId;
	}
	public void setErrorId(int errorId) {
		this.errorId = errorId;
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
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public String getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	public String getErrorNbr() {
		return errorNbr;
	}
	public void setErrorNbr(String errorNbr) {
		this.errorNbr = errorNbr;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
