package com.hp.it.perf.ac.load.process;

import java.util.Comparator;

public class PivotSortField<T> {

	private String name;
	private Comparator<T> comparator;
	private Class<T> fieldType;
	private boolean reverse;

	public PivotSortField(PivotViewBuilder pivotViewBuilder,
			Comparator<T> comparator, Class<T> fieldType) {
		this.comparator = comparator;
		this.fieldType = fieldType;
	}

	public void setName(String fieldName) {
		this.name = fieldName;
	}

	public String getName() {
		return name;
	}

	public Comparator<T> getComparator() {
		return comparator;
	}

	public Class<T> getFieldType() {
		return fieldType;
	}

	protected int compare(Object v1, Object v2) {
		int result = comparator.compare(fieldType.cast(v1), fieldType.cast(v2));
		return reverse ? -result : result;
	}

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

}
