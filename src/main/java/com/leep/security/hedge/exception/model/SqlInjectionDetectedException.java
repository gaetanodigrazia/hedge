package com.leep.security.hedge.exception.model;

public class SqlInjectionDetectedException extends RuntimeException {
    public SqlInjectionDetectedException(String field) {
        super("SQL injection detected in field: " + field);
    }
}