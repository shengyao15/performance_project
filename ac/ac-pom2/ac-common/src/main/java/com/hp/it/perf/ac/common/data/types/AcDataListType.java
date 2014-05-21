package com.hp.it.perf.ac.common.data.types;

import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.util.List;

import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataOutput;
import com.hp.it.perf.ac.common.data.AcDataType;

class AcDataListType implements AcDataType {

	@SuppressWarnings("rawtypes")
	private Class<? extends List> objectClasz;
	private Constructor<?> constructor;

	public AcDataListType(Class<?> listClasz) {
		this.objectClasz = listClasz.asSubclass(List.class);
	}

	@Override
	public int getGlobalDataType() {
		return AcDataTypeConstants.TYPE_LIST;
	}

	@Override
	public Class<?> getObjectClass() {
		return objectClasz;
	}

	@Override
	public void writeObject(AcDataOutput out, Object obj) throws IOException {
		List<?> list = objectClasz.cast(obj);
		out.writeInt(list.size());
		for (Object element : list) {
			out.writeObject(element);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object readObject(AcDataInput in) throws IOException,
			ClassNotFoundException {
		List list;
		try {
			if (this.constructor == null) {
				// speed up new instance
				this.constructor = getObjectClass().getConstructor();
				this.constructor.setAccessible(true);
			}
			list = objectClasz.cast(constructor.newInstance());
		} catch (Exception e) {
			throw (InvalidClassException) (new InvalidClassException(
					getObjectClass().getName(), e.getMessage()).initCause(e));
		}
		int size = in.readInt();
		for (int i = 0; i < size; i++) {
			list.add(in.readObject());
		}
		return list;
	}

}
