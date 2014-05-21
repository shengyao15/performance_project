package com.hp.it.perf.ac.load.parse.element;

import java.util.ArrayList;
import java.util.List;

import com.hp.it.perf.ac.load.bind.AcBinder;
import com.hp.it.perf.ac.load.bind.AcBindingException;
import com.hp.it.perf.ac.load.parse.AcTextElement;

public class AcArrayElement implements AcTextElement {

	private List<AcTextElement> list;
	
	private final String name;
	
	public AcArrayElement(String name) {
		this.name = name;
	}
	
	@Override
	public String getElementName() {
		return name;
	}

	public void addChild(AcTextElement child) {
		if (list == null) {
			list = new ArrayList<AcTextElement>();
		}
		list.add(child);
	}

	@Override
	public Object bind(AcBinder binder) throws AcBindingException {
		Object array = binder.create(list == null ? 0 : list.size());
		// loop all children
		if (list != null) {
			for (int i = 0, n = list.size(); i < n; i++) {
				AcTextElement child = list.get(i);
				binder.bindProperty(array, i, child);
			}
		}
		return array;
	}

}
