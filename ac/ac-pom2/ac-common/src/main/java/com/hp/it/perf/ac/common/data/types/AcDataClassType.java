package com.hp.it.perf.ac.common.data.types;

import java.io.IOException;

import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataOutput;
import com.hp.it.perf.ac.common.data.AcDataType;

class AcDataClassType implements AcDataType {

	@Override
	public int getGlobalDataType() {
		return AcDataTypeConstants.TYPE_CLASS;
	}

	@Override
	public Class<?> getObjectClass() {
		return Class.class;
	}

	@Override
	public void writeObject(AcDataOutput out, Object obj) throws IOException {
		Class<?> clasz = (Class<?>) obj;
		out.writeString(clasz.getName());
	}

	@Override
	public Object readObject(AcDataInput in) throws IOException,
			ClassNotFoundException {
		String className = in.readString();
		return Class.forName(className);
	}

}
