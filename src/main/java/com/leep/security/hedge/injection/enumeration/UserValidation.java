package com.leep.security.hedge.injection.enumeration;

public enum UserValidation {
    NONE(-1),
    UUID(36),
    EMAIL(320),
    TOKEN(64);

    private final int maxChar;

    UserValidation(int maxChar) {
        this.maxChar = maxChar;
    }

    public int getMaxChar() {
        return maxChar;
    }
}