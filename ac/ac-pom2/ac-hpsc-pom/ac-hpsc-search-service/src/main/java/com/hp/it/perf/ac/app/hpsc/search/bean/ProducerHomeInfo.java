package com.hp.it.perf.ac.app.hpsc.search.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
@Entity
public class ProducerHomeInfo implements Serializable{

	private static final long serialVersionUID = -4466047826573718758L;
	@Id
	@Column(name="feature")
	private String feature;
	@Column(name="total")
	private int total;
	@Column(name="fail")
	private int fail;
	@Column(name="failRate")
	private double failRate;

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getFail() {
		return fail;
	}

	public void setFail(int fail) {
		this.fail = fail;
	}

	public double getFailRate() {
		return failRate;
	}

	public void setFailRate(double failRate) {
		this.failRate = failRate;
	}
	
}
