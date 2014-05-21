package com.hp.it.perf.ac.load.common;

public interface AcMapper<K, V> {
	public V apply(K object);
}
