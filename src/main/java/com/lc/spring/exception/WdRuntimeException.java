package com.lc.spring.exception;

public class WdRuntimeException extends RuntimeException {

    public WdRuntimeException(String message) {
        super(message);
    }

    public WdRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
