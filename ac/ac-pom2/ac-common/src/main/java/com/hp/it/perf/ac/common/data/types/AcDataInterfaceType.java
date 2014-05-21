package com.hp.it.perf.ac.common.data.types;

import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.Constructor;

import com.hp.it.perf.ac.common.data.AcData;
import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataOutput;
import com.hp.it.perf.ac.common.data.AcDataType;

class AcDataInterfaceType implements AcDataType {

	private Constructor<?> acDataConstructor;
	private Class<?> objectClasz;

	public AcDataInterfaceType(Class<?> acDataClasz) {
		this.objectClasz = acDataClasz;
	}

	@Override
	public int getGlobalDataType() {
		return AcDataTypeConstants.TYPE_ACDATA_INTERFACE;
	}

	@Override
	public Class<?> getObjectClass() {
		return objectClasz;
	}

	@Override
	public void writeObject(AcDataOutput out, Object obj) throws IOException {
		((AcData) obj).toOutput(out);
	}

	@Override
	public Object readObject(AcDataInput in) throws IOException,
			ClassNotFoundException {
		AcData acData;
		try {
			if (this.acDataConstructor == null) {
				// speed up new instance
				this.acDataConstructor = getObjectClass()
						.getDeclaredConstructor();
				this.acDataConstructor.setAccessible(true);
			}
			acData = (AcData) acDataConstructor.newInstance();
		} catch (Exception e) {
			throw (InvalidClassException) (new InvalidClassException(
					getObjectClass().getName(), e.getMessage()).initCause(e));
		}
		acData.fromInput(in);
		return acData;
	}

}
