package com.hp.it.perf.ac.load.process;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.hp.it.perf.ac.load.common.AcMapper;
import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.common.AcReduceCallback;

public class PivotViewBuilder {

	protected List<PivotFilter> filters = new ArrayList<PivotFilter>();
	protected List<PivotGroupField> groups = new ArrayList<PivotGroupField>();
	protected List<PivotValueField> values = new ArrayList<PivotValueField>();
	protected List<PivotSortField<?>> sorts = new ArrayList<PivotSortField<?>>();
	protected List<PivotJoinField> joins = new ArrayList<PivotJoinField>();
	protected List<PivotDataType> dataTypes = new ArrayList<PivotDataType>();

	public PivotFilter addFilter(String filterName, AcPredicate<Object> filter) {
		PivotFilter pfilter = new PivotFilter(this, filter);
		pfilter.setName(filterName);
		filters.add(pfilter);
		return pfilter;
	}

	public PivotGroupField addGroupField(String fieldName,
			AcMapper<Object, ?> mapper) {
		PivotGroupField gField = new PivotGroupField(this, mapper);
		gField.setName(fieldName);
		groups.add(gField);
		return gField;
	}

	public PivotValueField addValueField(String fieldName,
			AcReduceCallback<Object, ?> reducer) {
		PivotValueField vField = new PivotValueField(this, reducer);
		vField.setName(fieldName);
		values.add(vField);
		return vField;
	}

	public <T> PivotSortField<T> addSortField(String fieldName,
			Comparator<T> comparator, Class<T> fieldType) {
		PivotSortField<T> sField = new PivotSortField<T>(this, comparator,
				fieldType);
		sField.setName(fieldName);
		sorts.add(sField);
		return sField;
	}

	public <T extends Comparable<T>> PivotSortField<T> addSortField(
			String fieldName, Class<T> fieldType) {
		PivotSortField<T> sField = new PivotSortField<T>(this,
				new PivotComparator<T>(), fieldType);
		sField.setName(fieldName);
		sorts.add(sField);
		return sField;
	}

	public PivotDataType addDataType(String dataTypeName, Class<?> dataTypeClass) {
		PivotDataType dataType = new PivotDataType(this, dataTypeName, dataTypeClass);
		dataTypes.add(dataType);
		return dataType;
	}

	public PivotJoinField addJoinField(String fieldName, String dataTypeName1,
			String dataTypeName2) {
		PivotJoinField jField = new PivotJoinField(this);
		jField.setName(fieldName);
		jField.setJoinBy(dataTypeName1, fieldName);
		jField.setJoinBy(dataTypeName2, fieldName);
		joins.add(jField);
		return jField;
	}

	public PivotView pivot(Iterator<?> data) {
		return new PivotViewImpl(this, data);
	}

	public PivotViewCallback pivotCallback() {
		final PivotViewImpl view = new PivotViewImpl(this);
		return new PivotViewCallback() {
			private int index = 0;
			private boolean end = false;

			@Override
			public void apply(Object data) {
				if (end) {
					throw new IllegalStateException("view is created");
				}
				view.processEach(data, index++);
			}

			@Override
			public PivotView createView() {
				if (!end) {
					view.processEnd();
					end = true;
				}
				return view;
			}
		};
	}

}
