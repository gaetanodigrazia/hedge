package com.leep.security.hedge.exception.model;

public class OsInjectionDetectedException extends RuntimeException {
    public OsInjectionDetectedException(String field) {
        super("OS injection detected in field: " + field);
    }
}