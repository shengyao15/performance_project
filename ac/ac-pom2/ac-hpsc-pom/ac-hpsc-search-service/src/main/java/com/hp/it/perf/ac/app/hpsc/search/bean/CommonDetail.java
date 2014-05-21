package com.hp.it.perf.ac.app.hpsc.search.bean;

import java.io.Serializable;
import java.util.Date;

public class CommonDetail implements Serializable {

	private static final long serialVersionUID = -6125179188918884898L;

	private long acid;
	
	private Date created;
	
	private int duration;
	
	private String name;

	public long getAcid() {
		return acid;
	}

	public void setAcid(long acid) {
		this.acid = acid;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
