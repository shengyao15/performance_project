package com.hp.it.perf.ac.load.set.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import com.hp.it.perf.ac.load.set.AcSet;
import com.hp.it.perf.ac.load.set.AcSetItem;
import com.hp.it.perf.ac.load.set.AcSetProcessHandler;

class KeySetHandler {

	private AcSetProcessHandler handler;
	private Map<Object, SimpleAcSet> valueSets = new LinkedHashMap<Object, SimpleAcSet>();

	public void setProcessHandler(AcSetProcessHandler handler) {
		this.handler = handler;
	}

	public void onStart(AcSet set) {
		handler.onStart(set);
	}

	public void handleKeyItem(AcSetItem item, AcSet set, Object origin) {
		SimpleAcSet valueSet = new SimpleAcSet(null);// TODO
		valueSet.setKey(item.getValue());
		valueSet.setEnclosingItem(item);
		valueSets.put(item.getValue(), valueSet);
		// omit key set event
		handler.handle(item, set, origin);
		// omit value set start event
		handler.onStart(valueSet);
	}

	public void handleValueItem(AcSetItem item, AcSet set, Object origin) {
		handler.handle(item, set, origin);
	}

	public void onEnd(AcSet set) {
		for (AcSet valueSet : valueSets.values()) {
			handler.onEnd(valueSet);
		}
		handler.onEnd(set);
	}

	public SimpleAcSet getValueSet(Object key) {
		return valueSets.get(key);
	}

}