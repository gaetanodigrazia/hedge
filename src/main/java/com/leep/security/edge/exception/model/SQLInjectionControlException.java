package com.leep.security.edge.exception.model;

public class SQLInjectionControlException extends RuntimeException {
    public SQLInjectionControlException(String message) {
        super(message);
    }
}
