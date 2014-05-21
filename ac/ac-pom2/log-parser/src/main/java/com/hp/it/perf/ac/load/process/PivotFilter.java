package com.hp.it.perf.ac.load.process;

import com.hp.it.perf.ac.load.common.AcPredicate;

public class PivotFilter {

	private AcPredicate<Object> filter;
	private String name;

	public PivotFilter(PivotViewBuilder builder, AcPredicate<Object> filter) {
		this.filter = filter;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	protected boolean accept(Object obj) {
		return filter.apply(obj);
	}
	
	public String getName() {
		return this.name;
	}

}
