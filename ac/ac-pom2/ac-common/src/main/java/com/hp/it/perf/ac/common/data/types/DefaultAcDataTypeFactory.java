package com.hp.it.perf.ac.common.data.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.it.perf.ac.common.data.AcData;
import com.hp.it.perf.ac.common.data.AcDataBean;
import com.hp.it.perf.ac.common.data.AcDataType;
import com.hp.it.perf.ac.common.data.AcDataTypeFactory;

public class DefaultAcDataTypeFactory implements AcDataTypeFactory {

	private static Map<Class<?>, AcDataType> factory = new HashMap<Class<?>, AcDataType>();
	private static Map<Integer, AcDataType> globalFactory = new HashMap<Integer, AcDataType>();

	static {
		init();
	}

	private static void init() {
		register(new AcDataStringType());
		register(new AcDataNullType());
		register(new AcDataClassType());
		register(new AcDataDateType());
		register(new AcDataDataObjectType());
	}

	private static void register(AcDataType dataType) {
		// TODO check duplicate
		factory.put(dataType.getObjectClass(), dataType);
		if (dataType.getGlobalDataType() != AcDataTypeConstants.TYPE_UNKNOWN) {
			// 0 is not checked in global
			globalFactory.put(dataType.getGlobalDataType(), dataType);
		}
	}

	@Override
	public AcDataType createDataType(Class<?> clasz) {
		return createDataType(clasz, clasz);
	}

	private AcDataType createDataType(Class<?> clasz, Class<?> originClasz) {
		AcDataType dataType = factory.get(clasz);
		if (dataType == null) {
			if (clasz == Object.class) {
				// not found any matched
				return processSpecialType(originClasz);
			} else {
				return createDataType(clasz.getSuperclass(), originClasz);
			}
		} else {
			return dataType;
		}
	}

	@Override
	public AcDataType createDataType(int globalDataType, String objectClassName)
			throws ClassNotFoundException {
		AcDataType dataType;
		if (globalDataType != AcDataTypeConstants.TYPE_UNKNOWN) {
			dataType = globalFactory.get(globalDataType);
			if (dataType != null) {
				// TODO maybe check object classname
				return dataType;
			}
		}
		if (objectClassName == null) {
			throw new ClassNotFoundException("empty class name");
		}
		Class<?> clz = Class.forName(objectClassName);
		return processSpecialType(clz);
	}

	protected AcDataType processSpecialType(Class<?> clz) {
		if (clz.isArray()) {
			return new AcDataArrayType(clz);
		} else if (clz.isEnum()) {
			return new AcDataEnumType(clz);
		} else if (AcData.class.isAssignableFrom(clz)) {
			return new AcDataInterfaceType(clz.asSubclass(AcData.class));
		} else if (clz.isAnnotationPresent(AcDataBean.class)) {
			return new AcDataBeanType(clz);
		} else if (List.class.isAssignableFrom(clz)) {
			return new AcDataListType(clz);
		} else {
			return new AcDataUnknownType(clz);
		}
	}
}
