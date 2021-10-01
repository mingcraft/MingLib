package com.mingcraft.minglib.exceptions.db;

public class MongoConnectionFailedException extends RuntimeException {

    public MongoConnectionFailedException(String message) {
        super(message);
    }

}
