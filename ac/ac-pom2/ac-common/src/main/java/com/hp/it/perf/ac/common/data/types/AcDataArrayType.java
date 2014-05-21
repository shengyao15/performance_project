package com.hp.it.perf.ac.common.data.types;

import java.io.IOException;
import java.lang.reflect.Array;

import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataOutput;
import com.hp.it.perf.ac.common.data.AcDataType;
import com.hp.it.perf.ac.common.data.AcDataUtils;

class AcDataArrayType implements AcDataType {

	private final Class<?> objectClasz;
	private final Class<?> componentClasz;

	public AcDataArrayType(Class<?> arrayClasz) {
		this.objectClasz = arrayClasz;
		this.componentClasz = arrayClasz.getComponentType();
	}

	@Override
	public int getGlobalDataType() {
		return AcDataTypeConstants.TYPE_ARRAY;
	}

	@Override
	public Class<?> getObjectClass() {
		return objectClasz;
	}

	@Override
	public void writeObject(AcDataOutput out, Object obj) throws IOException {
		// write array length
		int len = Array.getLength(obj);
		out.writeInt(len);
		// write each object content
		if (obj instanceof Object[]) {
			Object[] array = (Object[]) obj;
			for (int i = 0; i < len; i++) {
				AcDataUtils.writePrimitiveOrObject(out, array[i],
						componentClasz);
			}
		} else {
			// primitive
			for (int i = 0; i < len; i++) {
				AcDataUtils.writePrimitiveOrObject(out, Array.get(obj, i),
						componentClasz);
			}
		}
	}

	@Override
	public Object readObject(AcDataInput in) throws IOException,
			ClassNotFoundException {
		int len = in.readInt();
		Object obj = getObjectClass().cast(
				Array.newInstance(componentClasz, len));
		if (obj instanceof Object[]) {
			Object[] array = (Object[]) obj;
			for (int i = 0; i < len; i++) {
				Object value = AcDataUtils.readPrimitiveOrObject(in,
						componentClasz);
				array[i] = value;
			}
		} else {
			// primitive
			for (int i = 0; i < len; i++) {
				Object value = AcDataUtils.readPrimitiveOrObject(in,
						componentClasz);
				Array.set(obj, i, value);
			}
		}
		return obj;
	}

}
