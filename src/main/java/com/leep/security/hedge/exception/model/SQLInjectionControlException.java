package com.leep.security.hedge.exception.model;

public class SQLInjectionControlException extends RuntimeException {
    public SQLInjectionControlException(String message) {
        super(message);
    }
}
