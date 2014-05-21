package com.hp.it.perf.ac.common.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.hp.it.perf.ac.common.data.types.DefaultAcDataTypeFactory;

public class AcDataTypeManager {

	private static class InternalDataType implements AcDataType {

		private AcDataType internal;

		private int typeId;

		private boolean written;

		public InternalDataType(AcDataType internal) {
			this.internal = internal;
		}

		@Override
		public int getGlobalDataType() {
			return internal.getGlobalDataType();
		}

		@Override
		public Class<?> getObjectClass() {
			return internal.getObjectClass();
		}

		@Override
		public void writeObject(AcDataOutput output, Object obj)
				throws IOException {
			internal.writeObject(output, obj);
		}

		@Override
		public Object readObject(AcDataInput input) throws IOException,
				ClassNotFoundException {
			return internal.readObject(input);
		}

		public int getTypeId() {
			return typeId;
		}

		public void setTypeId(int typeId) {
			this.typeId = typeId;
		}

		public boolean isWritten() {
			return written;
		}

		public void setWritten(boolean written) {
			this.written = written;
		}

	}

	private Map<Class<?>, InternalDataType> cachedMapping = new HashMap<Class<?>, InternalDataType>();

	private Map<Integer, AcDataType> existingMapping = new HashMap<Integer, AcDataType>();

	private AcDataTypeFactory dataTypeFactory = new DefaultAcDataTypeFactory();

	private int currentTypeId = 0;

	public AcDataType getDataType(Class<?> clasz) {
		InternalDataType dataType = (InternalDataType) cachedMapping.get(clasz);
		if (dataType != null) {
			return dataType;
		}
		// check global id
		// if not, create one for this class
		dataType = new InternalDataType(dataTypeFactory.createDataType(clasz));
		dataType.setTypeId(++currentTypeId); // must not 0
		cachedMapping.put(clasz, dataType);
		return dataType;
	}

	public void writeDataClass(AcDataOutput out, AcDataType dataType)
			throws IOException {
		InternalDataType internalDataType = (InternalDataType) dataType;
		if (!internalDataType.isWritten()) {
			// write block of internal data type
			out.writeInt(0); // mark this is block of type def
			out.writeInt(internalDataType.getTypeId());
			out.writeInt(internalDataType.getGlobalDataType());
			Class<?> objectClass = internalDataType.getObjectClass();
			out.writeString(objectClass == null ? null : objectClass.getName());
			internalDataType.setWritten(true);
		} else {
			// write type id for flag
			out.writeInt(internalDataType.getTypeId());
		}
	}

	public AcDataType readDataType(AcDataInput in) throws IOException,
			ClassNotFoundException {
		int typeId = in.readInt();
		AcDataType dataType;
		if (typeId == 0) {
			// block of internal data type
			typeId = in.readInt();
			int globalDataType = in.readInt();
			String objectClassName = in.readString();
			dataType = dataTypeFactory.createDataType(globalDataType,
					objectClassName);
			existingMapping.put(typeId, dataType);
		} else {
			dataType = existingMapping.get(typeId);
			if (dataType == null) {
				throw new IOException("invalid type id: " + typeId);
			}
		}
		return dataType;
	}
}
