package com.hp.it.perf.ac.app.hpsc.storm.util;

import java.util.Map;

public interface IUpdater<U, V> {
	
	@SuppressWarnings("rawtypes")
	public void updateByDelegate(IUpdaterDelegate delegate, Map<U, V> db);
	
	@SuppressWarnings("rawtypes")
	public void updateByDelegate(IUpdaterDelegate delegate, Map<U, V> db, int category);

}
