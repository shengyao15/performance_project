package com.hp.it.perf.ac.service.transfer.data;

import java.io.Serializable;

import com.hp.it.perf.ac.common.data.AcDataBean;

@AcDataBean
public class AcDataFileSummary implements Serializable {

	private static final long serialVersionUID = 1L;

	private long lastModified;

	private long fileSize;

	private int successCount;

	private int errorCount;

	private int ignoredCount;

	private long duration;

	private int dataSessionId;

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getDataSessionId() {
		return dataSessionId;
	}

	public void setDataSessionId(int dataSessionId) {
		this.dataSessionId = dataSessionId;
	}

	public int getIgnoredCount() {
		return ignoredCount;
	}

	public void setIgnoredCount(int ignoredCount) {
		this.ignoredCount = ignoredCount;
	}

}
