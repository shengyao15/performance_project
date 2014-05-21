package com.hp.it.perf.ac.load.util;

public interface StatisticsUnit {

	public int add();

	public int add(String label);

	public int add(String label, String... otherLabels);

	public void setLong(long longValue);

	public void setInt(int intValue);

	public void setDouble(double doubleValue);

	public long getLong(int index);

	public int getInt(int index);

	public double getDouble(int index);

	public Number get(int index);

	public String[] getLabels(int index);

	public boolean hasLabel(int index, String label);

	public int count();

	public int count(String label, String... otherLabels);

	public int[] toIntArray();

	public int[] toIntArray(String label, String... otherLabels);

	public long[] toLongArray();

	public long[] toLongArray(String label, String... otherLabels);

	public double[] toDoubleArray();

	public double[] toDoubleArray(String label, String... otherLabels);

	public int[] indexesFor(String label, String... otherLabels);

	public String[] getLabels();

}
