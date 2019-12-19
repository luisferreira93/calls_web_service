package com.enterprise.luisferreira.utils;

public class Response {

    private int statusCode;
    private Object Body;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Object getBody() {
        return Body;
    }

    public void setBody(Object body) {
        Body = body;
    }

    @Override
    public String toString() {
        return "{\"Response\": [ {" +
                "\"statusCode\" : \"" + statusCode + "\","
                + "\"Body\" : \"" + Body + "\"" +
                "}]}";
    }
}
