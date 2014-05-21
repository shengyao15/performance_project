package com.hp.it.perf.ac.load.process;

import com.hp.it.perf.ac.load.common.AcMapper;

public class PivotGroupField {
	private String name;
	private AcMapper<Object, ?> mapper;

	public PivotGroupField(PivotViewBuilder pivotViewBuilder,
			AcMapper<Object, ?> mapper) {
		this.mapper = mapper;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected Object apply(Object data) {
		return mapper.apply(data);
	}

	public String getName() {
		return name;
	}

}
