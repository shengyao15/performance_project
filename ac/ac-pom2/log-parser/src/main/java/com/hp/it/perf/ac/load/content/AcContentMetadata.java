package com.hp.it.perf.ac.load.content;

import java.net.URI;

public class AcContentMetadata {
	private URI location;

	private long lastModified = 0;// UNKNON

	private String basename;

	private String signature;

	private long size = -1;// UNKNOWN

	private boolean reloadable = false;// DEFAULT is not reload-able

	public AcContentMetadata() {
		// default constructor
	}

	public AcContentMetadata(AcContentMetadata metadata) {
		location = metadata.location;
		lastModified = metadata.lastModified;
		basename = metadata.basename;
		signature = metadata.signature;
		size = metadata.size;
		reloadable = metadata.reloadable;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public URI getLocation() {
		return location;
	}

	public void setLocation(URI location) {
		this.location = location;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public String getBasename() {
		return basename;
	}

	public void setBasename(String basename) {
		this.basename = basename;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public boolean isReloadable() {
		return reloadable;
	}

	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	@Override
	public String toString() {
		return String
				.format("metadata{basename=%s, size=%s, lastModified=%s, reloadable=%s, location=%s}",
						basename, size, lastModified, reloadable, location);
	}

}
