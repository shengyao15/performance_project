package com.hp.it.perf.ac.common.realtime;

import java.io.Serializable;

public enum ValueType implements Serializable {
	
	TotalCount(1), ErrorCount(2), Score(3);
	
	private int index;
	
	private ValueType(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static ValueType getValueType(int index) {
		switch(index) {
			case 1: return TotalCount;
			case 2: return ErrorCount;
			case 3: return Score;
		}
			
		return null;
	}
	
}
