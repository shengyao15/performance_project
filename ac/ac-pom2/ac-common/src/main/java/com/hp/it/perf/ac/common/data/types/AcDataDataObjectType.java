package com.hp.it.perf.ac.common.data.types;

import java.io.IOException;

import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataObject;
import com.hp.it.perf.ac.common.data.AcDataOutput;
import com.hp.it.perf.ac.common.data.AcDataType;

class AcDataDataObjectType implements AcDataType {

	@Override
	public int getGlobalDataType() {
		return AcDataTypeConstants.TYPE_ACDATAOBJECT;
	}

	@Override
	public Class<?> getObjectClass() {
		return AcDataObject.class;
	}

	@Override
	public void writeObject(AcDataOutput out, Object obj) throws IOException {
		AcDataObject dataObject = (AcDataObject) obj;
		out.writeObject(dataObject.getObject());
	}

	@Override
	public Object readObject(AcDataInput in) throws IOException,
			ClassNotFoundException {
		return new AcDataObject(in.readObject());
	}

}
