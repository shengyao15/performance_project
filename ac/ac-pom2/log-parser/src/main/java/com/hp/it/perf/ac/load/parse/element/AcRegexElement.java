package com.hp.it.perf.ac.load.parse.element;

import java.util.regex.Matcher;

import com.hp.it.perf.ac.load.bind.AcBinder;
import com.hp.it.perf.ac.load.bind.AcBindingException;
import com.hp.it.perf.ac.load.parse.AcTextElement;

public class AcRegexElement implements AcTextElement {

	private Matcher matcher;
	private final String name;

	public AcRegexElement(String name, Matcher matcher) {
		this.name = name;
		this.matcher = matcher;
	}

	@Override
	public String getElementName() {
		return name;
	}

	@Override
	public Object bind(AcBinder binder) throws AcBindingException {
		Object[] parameters = new Object[matcher.groupCount()];
		for (int i = 0, n = parameters.length; i < n; i++) {
			parameters[i] = matcher.group(i + 1);
		}
		if (parameters.length == 1) {
			// 1 group -- 1 data
			return parameters[0];
		} else {
			// multiple group - more data (in method arguments)
			Object array = binder.create(parameters.length);
			// loop all children
			for (int i = 0, n = parameters.length; i < n; i++) {
				binder.bindProperty(array, i,
						new AcObjectElement(String.valueOf(i), parameters[i]));
			}
			return array;
		}
	}

}
