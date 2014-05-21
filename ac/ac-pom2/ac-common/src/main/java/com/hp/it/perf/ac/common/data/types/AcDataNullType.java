package com.hp.it.perf.ac.common.data.types;

import java.io.IOException;

import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataOutput;
import com.hp.it.perf.ac.common.data.AcDataType;

class AcDataNullType implements AcDataType {

	@Override
	public int getGlobalDataType() {
		return AcDataTypeConstants.TYPE_NULL;
	}

	@Override
	public Class<?> getObjectClass() {
		return null;
	}

	@Override
	public void writeObject(AcDataOutput out, Object obj) throws IOException {
	}

	@Override
	public Object readObject(AcDataInput in) throws IOException,
			ClassNotFoundException {
		return null;
	}

}
