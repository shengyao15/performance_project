package com.hp.it.perf.ac.common.model;

public interface AcDictionary {

	public AcCategory category(int code);

	public AcCategory[] categorys();
	
	public AcCategory category(String categoryName);

	public AcContextType contextType(int type);

	public AcContextType[] contextTypes();
	
	public AcContextType contextType(String typeName);

}