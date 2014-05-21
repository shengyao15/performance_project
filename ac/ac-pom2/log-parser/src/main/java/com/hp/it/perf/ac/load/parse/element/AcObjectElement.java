package com.hp.it.perf.ac.load.parse.element;

import com.hp.it.perf.ac.load.bind.AcBinder;
import com.hp.it.perf.ac.load.bind.AcBindingException;
import com.hp.it.perf.ac.load.parse.AcTextElement;

public class AcObjectElement implements AcTextElement {

	private Object bean;
	private final String name;

	public AcObjectElement(String name, Object bean) {
		this.name = name;
		this.bean = bean;
	}
	
	@Override
	public String getElementName() {
		return name;
	}

	@Override
	public Object bind(AcBinder binder) throws AcBindingException {
		return bean;
	}

//	@Override
//	public String toIndentString(String indent) {
//		return indent
//				+ bean
//				+ (bean == null ? " (null)"
//						: (" (" + bean.getClass().getName() + ")"));
//	}

}
