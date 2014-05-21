package com.hp.it.perf.ac.load.parse.element;

import com.hp.it.perf.ac.load.bind.AcBinder;
import com.hp.it.perf.ac.load.bind.AcBindingException;
import com.hp.it.perf.ac.load.parse.AcTextElement;

public class OneOfTextElement implements AcTextElement {

	private AcTextElement element;
	private int index;
	private final String name;

	public OneOfTextElement(String name, int index, AcTextElement element) {
		this.name = name;
		this.index = index;
		this.element = element;
	}

	@Override
	public String getElementName() {
		return name;
	}

	@Override
	public Object bind(AcBinder binder) throws AcBindingException {
		return this;
	}

	public AcTextElement getElement() {
		return element;
	}

	public int getIndex() {
		return index;
	}

}
