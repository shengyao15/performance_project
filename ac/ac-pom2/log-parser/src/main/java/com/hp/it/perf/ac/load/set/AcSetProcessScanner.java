package com.hp.it.perf.ac.load.set;

import java.util.Iterator;

public interface AcSetProcessScanner {

	public void scan(Iterator<?> iterator);

	public void setProcessHandler(AcSetProcessHandler handler);

	// TODO getHandler

}
