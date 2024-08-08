package com.craftelix.exception;

public class DaoException extends RuntimeException {

    public DaoException(Throwable throwable) {
        super(throwable);
    }
}
