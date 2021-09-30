package com.mingcraft.minglib.exceptions.log;

public class CallUnregisteredLoggerException extends RuntimeException {

    public CallUnregisteredLoggerException(String message) {
        super(message);
    }

}
