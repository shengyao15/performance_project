package com.hp.it.perf.ac.service.transfer.data;

import java.io.Serializable;

import com.hp.it.perf.ac.common.data.AcDataBean;

@AcDataBean
public class AcDataFileEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private String hostname;

	private String location;

	private String basename;

	private long lastModified;

	private long fileSize;
	
	private int dataSessionId;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getBasename() {
		return basename;
	}

	public void setBasename(String basename) {
		this.basename = basename;
	}

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

	public int getDataSessionId() {
		return dataSessionId;
	}

	public void setDataSessionId(int dataSessionId) {
		this.dataSessionId = dataSessionId;
	}
	
}
