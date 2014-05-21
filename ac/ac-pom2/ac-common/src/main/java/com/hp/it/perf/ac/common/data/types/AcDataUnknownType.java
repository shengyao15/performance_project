package com.hp.it.perf.ac.common.data.types;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataOutput;
import com.hp.it.perf.ac.common.data.AcDataType;

class AcDataUnknownType implements AcDataType {

	private final Class<?> objectClasz;

	public AcDataUnknownType(Class<?> unknownClass) {
		this.objectClasz = unknownClass;
	}

	@Override
	public int getGlobalDataType() {
		return AcDataTypeConstants.TYPE_UNKNOWN;
	}

	@Override
	public Class<?> getObjectClass() {
		return objectClasz;
	}

	@Override
	public void writeObject(AcDataOutput out, Object obj) throws IOException {
		if (obj instanceof Serializable) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.close();
			byte[] buffer = bos.toByteArray();
			out.writeInt(buffer.length);
			out.write(buffer);
		} else {
			throw new IOException("cannot write non-serializable object");
		}
	}

	@Override
	public Object readObject(AcDataInput in) throws IOException,
			ClassNotFoundException {
		int bufferLen = in.readInt();
		byte[] buffer = new byte[bufferLen];
		in.readFully(buffer);
		ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Object obj = ois.readObject();
		if (ois.read() < 0) {
			return getObjectClass().cast(obj);
		} else {
			throw new IOException("not get expected end of object stream");
		}
	}

}
