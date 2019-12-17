package com.enterprise.luisferreira.utils;

public enum CallType {

    INBOUND(0),
    OUTBOUND(1);

    private int type;

    CallType(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
