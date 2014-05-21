package com.hp.it.perf.ac.common.data.types;

import java.io.IOException;

import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataOutput;
import com.hp.it.perf.ac.common.data.AcDataType;

class AcDataStringType implements AcDataType {

	@Override
	public int getGlobalDataType() {
		return AcDataTypeConstants.TYPE_STRING;
	}

	@Override
	public Class<?> getObjectClass() {
		return String.class;
	}

	@Override
	public void writeObject(AcDataOutput out, Object obj) throws IOException {
		out.writeString((String) obj);
	}

	@Override
	public Object readObject(AcDataInput in) throws IOException,
			ClassNotFoundException {
		return in.readString();
	}

}
