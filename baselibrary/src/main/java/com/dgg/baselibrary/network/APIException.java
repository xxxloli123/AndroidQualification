package com.dgg.baselibrary.network;

/**
 * Created by xeq 2017/07/5.
 */

public class APIException extends Exception {
    public int code;
    public String message;
    public Object response;

    public APIException(int code, String message, Object response) {
        this.code = code;
        this.message = message;
        this.response = response;
    }

    @Override
    public String getMessage() {
        return message;
    }
}