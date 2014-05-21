package com.hp.it.perf.ac.load.util;

import java.util.HashMap;
import java.util.Map;

public final class StatisticsUnits {

	public static abstract class BitSetStatisticsUnit implements StatisticsUnit {

		protected int offset = 0;

		private Map<String, BitMap> labelsMap;

		protected abstract void inc(int minSize);

		protected abstract BitMap createBitSet();

		@Override
		public int add() {
			inc(offset + 1);
			return offset++;
		}

		@Override
		public int add(String label) {
			addLabel(label);
			return add();
		}

		private Map<String, BitMap> getLabelsMap() {
			if (labelsMap == null) {
				labelsMap = new HashMap<String, BitMap>();
			}
			return labelsMap;
		}

		protected void addLabel(String label) {
			if (label != null) {
				Map<String, BitMap> map = getLabelsMap();
				BitMap labelBitSet = map.get(label);
				if (labelBitSet == null) {
					labelBitSet = createBitSet();
					map.put(label, labelBitSet);
				}
				labelBitSet.set(offset);
			}
		}

		@Override
		public int add(String label, String... otherLabels) {
			addLabel(label);
			for (String oLabel : otherLabels) {
				addLabel(oLabel);
			}
			return add();
		}

		@Override
		public String[] getLabels(int index) {
			Map<String, BitMap> map = getLabelsMap();
			String[] allLabels = new String[map.size()];
			int i = 0;
			for (Map.Entry<String, BitMap> entry : map.entrySet()) {
				if (entry.getValue().get(index)) {
					allLabels[i++] = entry.getKey();
				}
			}
			String[] labels = new String[i];
			if (labels.length > 0) {
				System.arraycopy(allLabels, 0, labels, 0, labels.length);
			}
			return labels;
		}

		@Override
		public boolean hasLabel(int index, String label) {
			BitMap labelBitSet = getLabelsMap().get(label);
			if (labelBitSet != null) {
				return labelBitSet.get(index);
			} else {
				return false;
			}
		}

		@Override
		public int count() {
			return offset;
		}

		@Override
		public int count(String label, String... otherLabels) {
			BitMap bitset = createBitSet();
			bitset.or(bitSetFor(label));
			for (String oLabel : otherLabels) {
				bitset.and(bitSetFor(oLabel));
			}
			return bitset.cardinality();
		}

		public int[] indexesFor(String label, String... otherLabels) {
			BitMap bitset = createBitSet();
			bitset.or(bitSetFor(label));
			for (String oLabel : otherLabels) {
				bitset.and(bitSetFor(oLabel));
			}
			return toIndexes(bitset);
		}

		private int[] toIndexes(BitMap bitset) {
			return bitset.toArray();
		}

		public String[] getLabels() {
			Map<String, BitMap> map = getLabelsMap();
			return map.keySet().toArray(new String[map.size()]);
		}

		protected BitMap bitSetFor(String label) {
			BitMap labelBitSet = getLabelsMap().get(label);
			return labelBitSet == null ? createBitSet() : labelBitSet;
		}

		protected void checkSet() {
			if (offset == 0) {
				throw new IllegalStateException(
						"require call add() method first");
			}
		}

		protected int calNewSize(int length, int minSize) {
			int size = length + (length >> 1);
			if (size < minSize)
				size = minSize;
			// refer to ArrayList
			if (size > Integer.MAX_VALUE - 8) {
				size = Integer.MAX_VALUE - 8;
			}
			return size;
		}

		@Override
		public int[] toIntArray() {
			int[] array = new int[count()];
			for (int i = 0, n = array.length; i < n; i++) {
				array[i] = getInt(i);
			}
			return array;
		}

		@Override
		public int[] toIntArray(String label, String... otherLabels) {
			BitMap bitset = bitSetFor(label);
			bitset.or(bitSetFor(label));
			for (String oLabel : otherLabels) {
				bitset.and(bitSetFor(oLabel));
			}
			int[] bitsetArray = bitset.toArray();
			int arrayLen = bitsetArray.length;
			int[] array = new int[arrayLen];
			for (int i = 0; i < arrayLen; i++) {
				array[i] = getInt(bitsetArray[i]);
			}
			return array;
		}

		@Override
		public long[] toLongArray() {
			long[] array = new long[count()];
			for (int i = 0, n = array.length; i < n; i++) {
				array[i] = getLong(i);
			}
			return array;
		}

		@Override
		public long[] toLongArray(String label, String... otherLabels) {
			BitMap bitset = bitSetFor(label);
			bitset.or(bitSetFor(label));
			for (String oLabel : otherLabels) {
				bitset.and(bitSetFor(oLabel));
			}
			int[] bitsetArray = bitset.toArray();
			int arrayLen = bitsetArray.length;
			long[] array = new long[arrayLen];
			for (int i = 0; i < arrayLen; i++) {
				array[i] = getLong(bitsetArray[i]);
			}
			return array;
		}

		@Override
		public double[] toDoubleArray() {
			double[] array = new double[count()];
			for (int i = 0, n = array.length; i < n; i++) {
				array[i] = getDouble(i);
			}
			return array;
		}

		@Override
		public double[] toDoubleArray(String label, String... otherLabels) {
			BitMap bitset = bitSetFor(label);
			bitset.or(bitSetFor(label));
			for (String oLabel : otherLabels) {
				bitset.and(bitSetFor(oLabel));
			}
			int[] bitsetArray = bitset.toArray();
			int arrayLen = bitsetArray.length;
			double[] array = new double[arrayLen];
			for (int i = 0; i < arrayLen; i++) {
				array[i] = getDouble(bitsetArray[i]);
			}
			return array;
		}
	}

	private abstract static class IntegerStatisticsUnit extends
			BitSetStatisticsUnit {

		private int[] store;

		public IntegerStatisticsUnit(int size) {
			store = new int[size];
		}

		@Override
		public void setLong(long longValue) {
			setInt((int) longValue);
		}

		@Override
		public void setInt(int intValue) {
			checkSet();
			store[offset - 1] = intValue;
		}

		@Override
		public void setDouble(double doubleValue) {
			setInt((int) doubleValue);
		}

		@Override
		public long getLong(int index) {
			return store[index];
		}

		@Override
		public int getInt(int index) {
			return store[index];
		}

		@Override
		public double getDouble(int index) {
			return store[index];
		}

		@Override
		public Number get(int index) {
			return store[index];
		}

		@Override
		public int[] toIntArray() {
			int[] array = new int[count()];
			System.arraycopy(store, 0, array, 0, array.length);
			return array;
		}

		@Override
		protected void inc(int minSize) {
			if (store.length < minSize) {
				try {
					int[] newStore = new int[calNewSize(store.length, minSize)];
					System.arraycopy(store, 0, newStore, 0, store.length);
					store = newStore;
				} catch (OutOfMemoryError e) {
					System.err.println(minSize + " - " + store.length + " - "
							+ calNewSize(store.length, minSize));
					throw e;
				}
			}
		}

	}

	private abstract static class LongStatisticsUnit extends
			BitSetStatisticsUnit {

		private long[] store;

		public LongStatisticsUnit(int size) {
			store = new long[size];
		}

		@Override
		public void setLong(long longValue) {
			checkSet();
			store[offset - 1] = longValue;
		}

		@Override
		public void setInt(int intValue) {
			setLong(intValue);
		}

		@Override
		public void setDouble(double doubleValue) {
			setLong((long) doubleValue);
		}

		@Override
		public long getLong(int index) {
			return store[index];
		}

		@Override
		public int getInt(int index) {
			return (int) store[index];
		}

		@Override
		public double getDouble(int index) {
			return store[index];
		}

		@Override
		public Number get(int index) {
			return store[index];
		}

		@Override
		public long[] toLongArray() {
			long[] array = new long[count()];
			System.arraycopy(store, 0, array, 0, array.length);
			return array;
		}

		@Override
		protected void inc(int minSize) {
			if (store.length < minSize) {
				long[] newStore = new long[calNewSize(store.length, minSize)];
				System.arraycopy(store, 0, newStore, 0, store.length);
				store = newStore;
			}
		}

	}

	private abstract static class DoubleStatisticsUnit extends
			BitSetStatisticsUnit {

		private double[] store;

		public DoubleStatisticsUnit(int size) {
			store = new double[size];
		}

		@Override
		public void setLong(long longValue) {
			setDouble(longValue);
		}

		@Override
		public void setInt(int intValue) {
			setDouble(intValue);
		}

		@Override
		public void setDouble(double doubleValue) {
			checkSet();
			store[offset - 1] = doubleValue;
		}

		@Override
		public long getLong(int index) {
			return (long) store[index];
		}

		@Override
		public int getInt(int index) {
			return (int) store[index];
		}

		@Override
		public double getDouble(int index) {
			return store[index];
		}

		@Override
		public Number get(int index) {
			return store[index];
		}

		@Override
		public double[] toDoubleArray() {
			double[] array = new double[count()];
			System.arraycopy(store, 0, array, 0, array.length);
			return array;
		}

		@Override
		protected void inc(int minSize) {
			if (store.length < minSize) {
				double[] newStore = new double[calNewSize(store.length, minSize)];
				System.arraycopy(store, 0, newStore, 0, store.length);
				store = newStore;
			}
		}

	}

	// private access
	private StatisticsUnits() {
	}

	public static StatisticsUnit newFastIntStatisticsUnit() {
		return new IntegerStatisticsUnit(10) {

			@Override
			protected BitMap createBitSet() {
				return new BitSetMap();
			}

		};
	}

	public static StatisticsUnit newFastLongStatisticsUnit() {
		return new LongStatisticsUnit(10) {

			@Override
			protected BitMap createBitSet() {
				return new BitSetMap();
			}

		};
	}

	public static StatisticsUnit newFastDoubleStatisticsUnit() {
		return new DoubleStatisticsUnit(10) {

			@Override
			protected BitMap createBitSet() {
				return new BitSetMap();
			}

		};
	}

	public static StatisticsUnit newIntStatisticsUnit() {
		return new IntegerStatisticsUnit(10) {

			@Override
			protected BitMap createBitSet() {
				return new CompressedBitMap();
			}

		};
	}

	public static StatisticsUnit newLongStatisticsUnit() {
		return new LongStatisticsUnit(10) {

			@Override
			protected BitMap createBitSet() {
				return new CompressedBitMap();
			}

		};
	}

	public static StatisticsUnit newDoubleStatisticsUnit() {
		return new DoubleStatisticsUnit(10) {

			@Override
			protected BitMap createBitSet() {
				return new CompressedBitMap();
			}

		};
	}

	public static interface LongStatisticsFilter {
		public boolean accept(long testValue);
	}

	public static interface DoubleStatisticsFilter {
		public boolean accept(double testValue);
	}

	public static interface IntStatisticsFilter {
		public boolean accept(int testValue);
	}

	public static long[] filter(long[] values, LongStatisticsFilter filter) {
		int len = values.length;
		long[] data = new long[len];
		int index = 0;
		for (int i = 0; i < len; i++) {
			if (filter.accept(values[i])) {
				data[index++] = values[i];
			}
		}
		long[] result = new long[index];
		System.arraycopy(data, 0, result, 0, index);
		return result;
	}

	public static double[] filter(double[] values, DoubleStatisticsFilter filter) {
		int len = values.length;
		double[] data = new double[len];
		int index = 0;
		for (int i = 0; i < len; i++) {
			if (filter.accept(values[i])) {
				data[index++] = values[i];
			}
		}
		double[] result = new double[index];
		System.arraycopy(data, 0, result, 0, index);
		return result;
	}

	public static int[] filter(int[] values, IntStatisticsFilter filter) {
		int len = values.length;
		int[] data = new int[len];
		int index = 0;
		for (int i = 0; i < len; i++) {
			if (filter.accept(values[i])) {
				data[index++] = values[i];
			}
		}
		int[] result = new int[index];
		System.arraycopy(data, 0, result, 0, index);
		return result;
	}

}
