package com.hp.it.perf.ac.load.set.impl;

import java.util.Iterator;

import com.hp.it.perf.ac.load.set.AcSetProcessHandler;
import com.hp.it.perf.ac.load.set.AcSetProcessScanner;

public class AcMapperSetProcessScanner implements AcSetProcessScanner {

	private KeySetHandler keySetHandler = new KeySetHandler();

	private AcMapperSetType<?> setType;

	SimpleAcSet keySet = null;
	public AcMapperSetProcessScanner(AcMapperSetType<?> mapperType) {
		this.setType = mapperType;
	}

	@Override
	public void scan(Iterator<?> iterator) {
		int globalIndex = -1;

		while (iterator.hasNext()) {
			Object originItem = iterator.next();
			globalIndex++;
			processEach(originItem, globalIndex);
		}
		// notify done
		keySetHandler.onEnd(keySet);
	}

	protected void processEach(Object originItem, int globalIndex) {
		// TODO map to multiple keys?(word count, index)
		Object key = setType.safeMap(originItem);
		// (request to) ignore
		if (key == null) {
			return;
		}
		// perform key set
		if (keySet == null) {
			keySet = new SimpleAcSet(null);// TODO
			keySet.setKey(null);
			keySetHandler.onStart(keySet);
		}
		SimpleAcSet valueSet = keySetHandler.getValueSet(key);
		if (valueSet == null) {
			// new key found
			SimpleAcSetItem keySetItem = new SimpleAcSetItem(keySet);
			keySetItem.setValue(key);
			keySetItem.setOriginIndex(globalIndex);
			keySet.addItem(keySetItem);
			// omit key set event
			// omit value set start event
			keySetHandler.handleKeyItem(keySetItem, keySet, originItem);
		} else {
			// existing key
			SimpleAcSetItem valueSetItem = new SimpleAcSetItem(valueSet);
			valueSetItem.setValue(originItem);
			valueSetItem.setOriginIndex(globalIndex);
			valueSet.addItem(valueSetItem);
			// omit value set event
			keySetHandler.handleValueItem(valueSetItem, valueSet,
					originItem);
		}
	}

	@Override
	public void setProcessHandler(AcSetProcessHandler handler) {
		this.keySetHandler.setProcessHandler(handler);
	}

}
