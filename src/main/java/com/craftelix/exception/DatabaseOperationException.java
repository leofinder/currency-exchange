package com.craftelix.exception;

public class DatabaseOperationException extends RuntimeException {

    public DatabaseOperationException(Throwable throwable) {
        super(throwable);
    }
}
