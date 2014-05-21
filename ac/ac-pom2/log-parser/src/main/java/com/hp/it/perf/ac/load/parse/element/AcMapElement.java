package com.hp.it.perf.ac.load.parse.element;

import com.hp.it.perf.ac.load.bind.AcBinder;
import com.hp.it.perf.ac.load.bind.AcBindingException;
import com.hp.it.perf.ac.load.parse.AcTextElement;

public class AcMapElement implements AcTextElement {

	private String[] childNames;
	private AcTextElement[] children;
	private final String name;

	public AcMapElement(String name, int size) {
		this.name = name;
		childNames = new String[size];
		children = new AcTextElement[size];
	}

	@Override
	public String getElementName() {
		return name;
	}

	@Override
	public Object bind(AcBinder binder) throws AcBindingException {
		Object bean = binder.create();
		// loop all children
		for (int i = 0; i < childNames.length; i++) {
			String name = childNames[i];
			if (name == null)
				continue;
			AcTextElement child = children[i];
			try {
				binder.bindProperty(bean, name, child);
			} catch (Exception e) {
				throw new AcBindingException("binding property '" + name
						+ "' error", e);
			}
		}
		return bean;
	}

	public void addChild(int index, String name, AcTextElement child) {
		childNames[index] = name;
		children[index] = child;
	}

	// public String toIndentString(String indent) {
	// StringBuffer buffer = new StringBuffer();
	// buffer.append(indent).append(AcMapElement.class.getSimpleName());
	// indent += " ";
	// for (int i = 0; i < names.length; i++) {
	// String name = names[i];
	// if (name == null)
	// continue;
	// AcTextElement child = children[i];
	// buffer.append('\n').append(indent);
	// buffer.append(name).append(": \n");
	// buffer.append(child.toIndentString(indent + " "));
	// }
	// return buffer.toString();
	// }

}
