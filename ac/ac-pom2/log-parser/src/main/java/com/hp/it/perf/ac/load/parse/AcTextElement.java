package com.hp.it.perf.ac.load.parse;

import com.hp.it.perf.ac.load.bind.AcBinder;
import com.hp.it.perf.ac.load.bind.AcBindingException;

public interface AcTextElement {
	
	public String getElementName();

	public Object bind(AcBinder binder) throws AcBindingException;

//	public String toIndentString(String indent);
}
