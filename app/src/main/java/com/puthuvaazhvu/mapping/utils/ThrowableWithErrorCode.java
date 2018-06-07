package com.puthuvaazhvu.mapping.utils;

/**
 * Created by muthuveerappans on 06/06/18.
 */

public class ThrowableWithErrorCode extends Throwable {
    private final int errorCode;

    public ThrowableWithErrorCode(String message, int code) {
        super(message);
        this.errorCode = code;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
