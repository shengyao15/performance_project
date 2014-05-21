package com.hp.it.perf.ac.load.set.impl;

import com.hp.it.perf.ac.load.set.AcSet;
import com.hp.it.perf.ac.load.set.AcSetItem;
import com.hp.it.perf.ac.load.set.AcSetType;

public class SimpleAcSet implements AcSet {

	private AcSetType setType;
	private Object id;
	private int itemCount;
	private AcSetItem enclosingItem;

	public SimpleAcSet(AcSetType setType) {
		this.setType = setType;
	}

	@Override
	public AcSetType getType() {
		return setType;
	}

	@Override
	public Object getKey() {
		return id;
	}

	public void setKey(Object id) {
		this.id = id;
	}

	@Override
	public int size() {
		return itemCount;
	}

	public void addItem(AcSetItem setItem) {
		itemCount++;
	}

	@Override
	public AcSetItem getEnclosingItem() {
		return enclosingItem;
	}

	public void setEnclosingItem(AcSetItem enclosingItem) {
		this.enclosingItem = enclosingItem;
	}

}
