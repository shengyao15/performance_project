package com.hp.it.innovation.collaboration.common;

public enum UserStatusEnum {
    ACTIVE(1),
    INACTIVE(2);
    
    private int value;
    
    private UserStatusEnum(int value) {
        this.setValue(value);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
