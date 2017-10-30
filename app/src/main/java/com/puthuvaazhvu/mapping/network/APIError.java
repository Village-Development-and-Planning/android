package com.puthuvaazhvu.mapping.network;

public class APIError {

    private int statusCode = -1;
    private String message = "N/A";

    public APIError() {
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }
}