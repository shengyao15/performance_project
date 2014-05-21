package com.hp.it.perf.ac.common.data;

import java.io.IOException;

public interface AcDataType {

	public int getGlobalDataType();

	public Class<?> getObjectClass();

	public void writeObject(AcDataOutput out, Object obj) throws IOException;

	public Object readObject(AcDataInput in) throws IOException,
			ClassNotFoundException;

}
