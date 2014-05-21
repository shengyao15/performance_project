package com.hp.it.perf.ac.load.process;

import java.util.HashMap;
import java.util.Map;

public class PivotJoinField {

	private String name;

	private Map<String, String> joinFields;

	public PivotJoinField(PivotViewBuilder pivotViewBuilder) {
		this.joinFields = new HashMap<String, String>();
	}

	public void setName(String fieldName) {
		this.name = fieldName;
	}

	public void setJoinBy(String dataTypeName, String fieldName) {
		joinFields.put(dataTypeName, fieldName);
	}

	public String getName() {
		return name;
	}

}
