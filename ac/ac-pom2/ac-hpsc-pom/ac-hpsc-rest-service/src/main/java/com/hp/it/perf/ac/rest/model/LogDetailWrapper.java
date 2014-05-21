package com.hp.it.perf.ac.rest.model;

import org.codehaus.jackson.annotate.JsonValue;

import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;

public class LogDetailWrapper extends AcCommonDataWithPayLoad {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3421491276260037040L;

	public LogDetailWrapper() {
		super();
	}

	public LogDetailWrapper(AcCommonDataWithPayLoad clone) {
		this.setAcid(clone.getAcid());
		this.setName(clone.getName());
		this.setCreated(clone.getCreated());
		this.setDuration(clone.getDuration());
		this.setRefAcid(clone.getRefAcid());
		this.setContexts(clone.getContexts());
		this.setPayLoad(clone.getPayLoad());
		this.setLocation(clone.getLocation());
	}

	@JsonValue
	private Object toValue() {
		return getPayLoad();
	}
}
