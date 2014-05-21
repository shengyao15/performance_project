package com.hp.it.perf.ac.load.set.impl;

import com.hp.it.perf.ac.load.set.AcSet;
import com.hp.it.perf.ac.load.set.AcSetItem;

public class SimpleAcSetItem implements AcSetItem {

	private int originIndex;
	private int setIndex;
	private AcSet set;
	private Object value;

	public SimpleAcSetItem(AcSet set) {
		this.set = set;
		this.setIndex = set.size();
	}

	@Override
	public int getIndex() {
		return setIndex;
	}

	@Override
	public AcSet getSet() {
		return set;
	}

	@Override
	public int getOriginIndex() {
		return originIndex;
	}

	public void setOriginIndex(int originIndex) {
		this.originIndex = originIndex;
	}

	@Override
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
