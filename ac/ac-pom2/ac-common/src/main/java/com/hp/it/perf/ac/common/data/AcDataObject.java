package com.hp.it.perf.ac.common.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class AcDataObject implements AcData, Serializable {

	private static final long serialVersionUID = 1L;

	private Object object;

	public AcDataObject(Object object) {
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	@Override
	public void toOutput(AcDataOutput out) throws IOException {
		// this should not called due to specified type will handle it
		throw new IllegalStateException("shoud not invoke this");
	}

	@Override
	public void fromInput(AcDataInput in) throws IOException,
			ClassNotFoundException {
		// this should not called due to specified type will handle it
		throw new IllegalStateException("shoud not invoke this");
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		if (object instanceof AcData) {
			out.writeByte(1);
			((AcData) object).toOutput(new AcDataOutput(out));
		} else {
			out.writeByte(0);
			out.defaultWriteObject();
		}
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		byte flag = in.readByte();
		switch (flag) {
		case 1:
			object = new AcDataInput(in).readObject();
			break;
		default:
			in.defaultReadObject();
			break;
		}
	}

}
