package com.hp.it.perf.ac.load.bind;

import java.util.ArrayList;
import java.util.List;

import com.hp.it.perf.ac.load.parse.element.OneOfTextElement;

class OneOfBindingAccessor implements AcBindingAccessor {

	private List<AcBindingAccessor> childs = new ArrayList<AcBindingAccessor>();

	@Override
	public AcBinder getTypeBuilder() {
		return null;
	}

	@Override
	public void invokeAccessor(Object bean, Object value)
			throws AcBindingException {
		OneOfTextElement element = (OneOfTextElement) value;
		AcBindingAccessor accessor = childs.get(element.getIndex());
		Object childValue = element.getElement()
				.bind(accessor.getTypeBuilder());
		accessor.invokeAccessor(bean, childValue);
	}

	public void addBinder(AcBindingAccessor cBinder) {
		childs.add(cBinder);
	}

}
