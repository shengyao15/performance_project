package com.hp.it.perf.ac.load.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.it.perf.ac.load.util.StatisticsUnit;
import com.hp.it.perf.ac.load.util.StatisticsUnits;

public class PivotViewImpl implements PivotView {

	private PivotViewBuilder builder;
	private Map<PivotGroupKeys, PivotGroupValues> groupMap = new HashMap<PivotGroupKeys, PivotGroupValues>();
	private List<Map.Entry<PivotGroupKeys, PivotGroupValues>> results = new ArrayList<Map.Entry<PivotGroupKeys, PivotGroupValues>>();
	private int groupLength = 0;

	private final static class PivotGroupKeys {
		private final Object[] keys;
		private int hash = 0;

		public PivotGroupKeys(Object[] keys) {
			this.keys = keys;
		}

		public Object[] getKeys() {
			return keys;
		}

		@Override
		public int hashCode() {
			if (hash == 0) {
				final int prime = 31;
				int result = 1;
				result = prime * result + Arrays.hashCode(keys);
				hash = result;
				return result;
			} else {
				return hash;
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PivotGroupKeys other = (PivotGroupKeys) obj;
			if (hashCode() != other.hashCode()
					|| !Arrays.equals(keys, other.keys))
				return false;
			return true;
		}

	}

	private final static class PivotGroupValues {

		private StatisticsUnit globalIndexes = StatisticsUnits
				.newIntStatisticsUnit();

		private Object[] reduceContexts;

		public PivotGroupValues(List<PivotValueField> values) {
			this.reduceContexts = new Object[values.size()];
			for (int i = 0, n = reduceContexts.length; i < n; i++) {
				PivotValueField field = values.get(i);
				reduceContexts[i] = field.getReducer().createReduceContext();
			}
		}

		public void aggregateValue(Object data, int globalIndex,
				List<PivotValueField> values) {
			globalIndexes.add();
			globalIndexes.setInt(globalIndex);
			for (int i = 0, n = reduceContexts.length; i < n; i++) {
				PivotValueField field = values.get(i);
				field.getReducer().reduce(data, reduceContexts[i]);
			}
		}

		public Object[] aggregateResult(List<PivotValueField> values) {
			for (int i = 0, n = reduceContexts.length; i < n; i++) {
				PivotValueField field = values.get(i);
				reduceContexts[i] = field.getReducer().getResult(
						reduceContexts[i]);
			}
			return reduceContexts;
		}

		public Object[] getResults() {
			return reduceContexts;
		}

	}

	public PivotViewImpl(PivotViewBuilder pivotViewBuilder, Iterator<?> data) {
		this.builder = pivotViewBuilder;
		int index = 0;
		while (data.hasNext()) {
			processEach(data.next(), index++);
		}
		processEnd();
	}

	public PivotViewImpl(PivotViewBuilder pivotViewBuilder) {
		this.builder = pivotViewBuilder;
	}

	public Iterator<Map<String, Object>> listAll() {
		final String[] keyNames = new String[builder.groups.size()];
		int i = 0;
		for (PivotGroupField gField : builder.groups) {
			keyNames[i++] = gField.getName();
		}
		final String[] valueNames = new String[builder.values.size()];
		i = 0;
		for (PivotValueField vField : builder.values) {
			valueNames[i++] = vField.getName();
		}
		return new Iterator<Map<String, Object>>() {
			private Iterator<Map.Entry<PivotGroupKeys, PivotGroupValues>> entries = results
					.iterator();

			@Override
			public boolean hasNext() {
				return entries.hasNext();
			}

			@Override
			public Map<String, Object> next() {
				Entry<PivotGroupKeys, PivotGroupValues> entry = entries.next();
				Map<String, Object> ret = new LinkedHashMap<String, Object>();
				Object[] keys = entry.getKey().getKeys();
				for (int i = 0; i < keyNames.length; i++) {
					ret.put(keyNames[i], keys[i]);
				}
				Object[] values = entry.getValue().getResults();
				for (int i = 0; i < valueNames.length; i++) {
					ret.put(valueNames[i], values[i]);
				}
				return ret;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	// public Iterator<Object> list(String name, Map<String, Object> criteria) {
	// }

	public void processEach(Object data, int globalIndex) {
		if (!isIgnored(data)) {
			PivotGroupKeys groupKeys = createGroupKeys(data);
			PivotGroupValues groupValues = groupMap.get(groupKeys);
			if (groupValues == null) {
				groupValues = new PivotGroupValues(builder.values);
				groupMap.put(groupKeys, groupValues);
			}
			groupValues.aggregateValue(data, globalIndex, builder.values);
		}
	}

	private static class PivotSorts implements
			Comparator<Map.Entry<PivotGroupKeys, PivotGroupValues>> {

		private PivotSortField<?>[] sortFields;
		private int[] fieldPosition; // positive - key, negative - value;

		public PivotSorts(PivotViewBuilder builder) {
			int sSize = builder.sorts.size();
			sortFields = new PivotSortField<?>[sSize];
			fieldPosition = new int[sSize];
			Map<String, Integer> map = new HashMap<String, Integer>();
			for (int i = 0; i < sSize; i++) {
				PivotSortField<?> sField = builder.sorts.get(i);
				if (sField.getName() == null) {
					throw new IllegalArgumentException(
							"sort field name is null");
				}
				if (map.put(sField.getName(), map.size()) != null) {
					throw new IllegalArgumentException(
							"duplicate sort field name: " + sField.getName());
				}
				sortFields[i] = sField;
			}
			for (int i = 0, n = builder.groups.size(); i < n && !map.isEmpty(); i++) {
				PivotGroupField gField = builder.groups.get(i);
				Integer sPos = map.remove(gField.getName());
				if (sPos != null) {
					fieldPosition[sPos.intValue()] = i + 1;
				}
			}
			for (int i = 0, n = builder.values.size(); i < n && !map.isEmpty(); i++) {
				PivotValueField vField = builder.values.get(i);
				Integer sPos = map.remove(vField.getName());
				if (sPos != null) {
					fieldPosition[sPos.intValue()] = -(i + 1);
				}
			}
			if (!map.isEmpty()) {
				throw new IllegalArgumentException(
						"sort field(s) are not defined: " + map.keySet());
			}
		}

		@Override
		public int compare(Map.Entry<PivotGroupKeys, PivotGroupValues> e1,
				Map.Entry<PivotGroupKeys, PivotGroupValues> e2) {
			for (int i = 0, n = sortFields.length; i < n; i++) {
				int pos = fieldPosition[i];
				Object v1, v2;
				if (pos > 0) {
					v1 = e1.getKey().keys[pos - 1];
					v2 = e2.getKey().keys[pos - 1];
				} else {
					v1 = e1.getValue().getResults()[-pos - 1];
					v2 = e2.getValue().getResults()[-pos - 1];
				}
				int compareResult = sortFields[i].compare(v1, v2);
				if (compareResult != 0) {
					// not same
					return compareResult;
				}
			}
			return 0;
		}
	}

	public void processEnd() {
		for (PivotGroupValues groupValues : groupMap.values()) {
			groupValues.aggregateResult(builder.values);
		}
		// process sorting
		results.addAll(groupMap.entrySet());
		groupMap = null;
		if (!builder.sorts.isEmpty()) {
			Collections.sort(results, new PivotSorts(builder));
		}
	}

	private PivotGroupKeys createGroupKeys(Object data) {
		if (groupLength == 0) {
			groupLength = builder.groups.size();
		}
		Object[] keys = new Object[groupLength];
		int i = 0;
		for (PivotGroupField field : builder.groups) {
			keys[i++] = field.apply(data);
		}
		return new PivotGroupKeys(keys);
	}

	protected boolean isIgnored(Object data) {
		boolean ignored = false;
		for (PivotFilter filter : builder.filters) {
			if (filter.accept(data)) {
				return false;
			}
			ignored = true;
		}
		return ignored;
	}

}
