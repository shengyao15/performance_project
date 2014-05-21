package com.hp.it.perf.ac.common.data.types;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataOutput;
import com.hp.it.perf.ac.common.data.AcDataType;
import com.hp.it.perf.ac.common.data.AcDataUtils;

class AcDataBeanType implements AcDataType {

	private List<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();
	private Constructor<?> constructor;
	private Class<?> objectClasz;

	public AcDataBeanType(Class<?> beanClasz) {
		objectClasz = beanClasz;
		// TODO SORTING?
		try {
			for (PropertyDescriptor pd : Introspector.getBeanInfo(beanClasz)
					.getPropertyDescriptors()) {
				if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
					pd.getReadMethod().setAccessible(true);
					pd.getWriteMethod().setAccessible(true);
					properties.add(pd);
				}
			}
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public int getGlobalDataType() {
		return AcDataTypeConstants.TYPE_ACDATABEAN;
	}

	@Override
	public Class<?> getObjectClass() {
		return objectClasz;
	}

	@Override
	public void writeObject(AcDataOutput out, Object obj) throws IOException {
		for (PropertyDescriptor pd : properties) {
			Object propertyValue;
			try {
				propertyValue = pd.getReadMethod().invoke(obj);
			} catch (Exception e) {
				throw (InvalidObjectException) new InvalidObjectException(
						"invoke read method error on property: " + pd)
						.initCause(e);
			}
			Class<?> propertyType = pd.getPropertyType();
			AcDataUtils
					.writePrimitiveOrObject(out, propertyValue, propertyType);
		}
	}

	@Override
	public Object readObject(AcDataInput in) throws IOException,
			ClassNotFoundException {
		Object obj;
		try {
			if (this.constructor == null) {
				// speed up new instance
				this.constructor = getObjectClass().getDeclaredConstructor();
				this.constructor.setAccessible(true);
			}
			obj = constructor.newInstance();
		} catch (Exception e) {
			throw (InvalidClassException) (new InvalidClassException(
					getObjectClass().getName(), e.getMessage()).initCause(e));
		}
		for (PropertyDescriptor pd : properties) {
			Class<?> propertyType = pd.getPropertyType();
			Object propertyValue = AcDataUtils.readPrimitiveOrObject(in,
					propertyType);
			try {
				pd.getWriteMethod().invoke(obj, propertyValue);
			} catch (Exception e) {
				throw (InvalidObjectException) new InvalidObjectException(
						"invoke write method error on property: " + pd)
						.initCause(e);
			}
		}
		return obj;
	}
}
