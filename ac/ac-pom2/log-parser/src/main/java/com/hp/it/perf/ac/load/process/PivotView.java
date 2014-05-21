package com.hp.it.perf.ac.load.process;

import java.util.Iterator;
import java.util.Map;

public interface PivotView {

	public Iterator<Map<String, Object>> listAll();
	
}
