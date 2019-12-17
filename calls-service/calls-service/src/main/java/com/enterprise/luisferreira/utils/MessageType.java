package com.enterprise.luisferreira.utils;

public enum MessageType {

    SUCCESS("Success");

    private String message;

    MessageType(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
