package com.hp.it.perf.ac.load.common;

import java.util.Iterator;

public interface AcReducer<K, V> {

	public V reduect(Iterator<K> itmes);

}
