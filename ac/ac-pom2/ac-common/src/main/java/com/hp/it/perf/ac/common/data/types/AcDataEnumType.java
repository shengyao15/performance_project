package com.hp.it.perf.ac.common.data.types;

import java.io.IOException;

import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataOutput;
import com.hp.it.perf.ac.common.data.AcDataType;

class AcDataEnumType implements AcDataType {

	private final Class<?> objectClasz;

	public AcDataEnumType(Class<?> enumClasz) {
		this.objectClasz = enumClasz;
	}

	@Override
	public int getGlobalDataType() {
		return AcDataTypeConstants.TYPE_ENUM;
	}

	@Override
	public Class<?> getObjectClass() {
		return objectClasz;
	}

	@Override
	public void writeObject(AcDataOutput out, Object obj) throws IOException {
		out.writeInt(((Enum<?>) obj).ordinal());
	}

	@Override
	public Object readObject(AcDataInput in) throws IOException,
			ClassNotFoundException {
		// TODO indexofbound error
		return getObjectClass().getEnumConstants()[in.readInt()];
	}
}
