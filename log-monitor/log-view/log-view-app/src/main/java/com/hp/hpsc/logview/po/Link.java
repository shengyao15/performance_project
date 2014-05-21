package com.hp.hpsc.logview.po;

public class Link {
	private String name;
	private String uri;
	private String lastModifiedDate;
	private String size;
	private boolean folderFlag;

	public boolean isFolderFlag() {
		return folderFlag;
	}

	public void setFolderFlag(boolean folderFlag) {
		this.folderFlag = folderFlag;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

}
