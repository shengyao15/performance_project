package com.hp.it.perf.ac.common.data.types;

import java.io.IOException;
import java.util.Date;

import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataOutput;
import com.hp.it.perf.ac.common.data.AcDataType;

class AcDataDateType implements AcDataType {

	@Override
	public int getGlobalDataType() {
		return AcDataTypeConstants.TYPE_DATE;
	}

	@Override
	public Class<?> getObjectClass() {
		return Date.class;
	}

	@Override
	public void writeObject(AcDataOutput out, Object obj) throws IOException {
		out.writeLong(((Date) obj).getTime());
	}

	@Override
	public Object readObject(AcDataInput in) throws IOException,
			ClassNotFoundException {
		return new Date(in.readLong());
	}

}
