package com.hp.it.perf.ac.common.model;

public interface AcCategory {
    public int code();

    public String name();

    public AcType[] types();

    public AcType type(int typeCode);
    
    public AcType type(String typeName);
    
    public boolean contains(AcType type);
    
    public boolean contains(String typename);
    
    public boolean contains(int typeCode);
    
    public AcLevel[] levels();
    
    public AcLevel level(int levelCode);
    
    public AcLevel level(String levelName);
    
    public boolean contains(AcLevel level);
    
    public String getPayloadClassName();
}
