package com.hp.it.perf.ac.common.data;

public interface AcDataTypeFactory {

	public AcDataType createDataType(Class<?> clasz);

	public AcDataType createDataType(int globalDataType, String className)
			throws ClassNotFoundException;

}
