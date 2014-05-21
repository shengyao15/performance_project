package com.hp.it.perf.ac.load.set;

public interface AcSet {

	public AcSetType getType();

	public Object getKey();

	public int size();
	
	public AcSetItem getEnclosingItem();

}
