package com.hp.it.perf.ac.load.parse.element;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.hp.it.perf.ac.load.bind.AcBinder;
import com.hp.it.perf.ac.load.bind.AcBindingException;
import com.hp.it.perf.ac.load.parse.AcTextElement;

public class AcNumberElement implements AcTextElement {

	private final Number number;
	private final String name;
	
	public AcNumberElement(String name, Number number) {
		this.name = name;
		this.number = number;
	}

	@Override
	public String getElementName() {
		return name;
	}
	
	@Override
	public Object bind(AcBinder binder) throws AcBindingException {
		Class<?> bindingType = binder.getBindingType();
		if (bindingType == int.class || bindingType == Integer.class) {
			return number.intValue();
		} else if (bindingType == long.class || bindingType == Long.class) {
			return number.longValue();
		} else if (bindingType == double.class || bindingType == Double.class) {
			return number.doubleValue();
		} else if (bindingType == byte.class || bindingType == Byte.class) {
			return number.byteValue();
		} else if (bindingType == float.class || bindingType == Float.class) {
			return number.floatValue();
		} else if (bindingType == short.class || bindingType == Short.class) {
			return number.shortValue();
		} else if (bindingType == BigInteger.class) {
			return new BigInteger(number.toString());
		} else if (bindingType == BigDecimal.class) {
			return new BigDecimal(number.toString());
		} else if (bindingType.isInstance(number)) {
			return number;
		} else {
			throw new AcBindingException("cannot conver to type " + bindingType);
		}
	}

//	@Override
//	public String toIndentString(String indent) {
//		return indent + number + " (Number)";
//	}

}
