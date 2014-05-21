package com.hp.it.perf.ac.load.process;

import com.hp.it.perf.ac.load.common.AcReduceCallback;

public class PivotValueField {

	private String name;
	private AcReduceCallback<Object, ?> reducer;

	public PivotValueField(PivotViewBuilder pivotViewBuilder,
			AcReduceCallback<Object, ?> reducer) {
		this.reducer = reducer;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected AcReduceCallback<Object, ?> getReducer() {
		return reducer;
	}

	public String getName() {
		return name;
	}

}
