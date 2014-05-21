package com.hp.it.perf.ac.service.chain.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

class LinkNodeIdCache<K, V> {

	private Map<K, V> store;

	public LinkNodeIdCache(final int capacity) {
		store = new LinkedHashMap<K, V>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Entry<K, V> eldest) {
				return size() > capacity;
			}
		};
	}

	public synchronized void put(K key, V value) {
		store.put(key, value);
	}

	public synchronized V get(K key) {
		return store.get(key);
	}

}
