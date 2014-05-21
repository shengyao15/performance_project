package com.hp.it.perf.ac.load.common;

public interface AcReduceCallback<K, V> {
	
	public Object createReduceContext();

	public void reduce(K item, Object context);

	public V getResult(Object context);
}
