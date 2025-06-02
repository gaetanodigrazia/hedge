package com.leep.security.hedge.injection;

public class SQLInjectionControlConfiguration {
    private static String message;

    public SQLInjectionControlConfiguration() {
    }

    public static String getMessage() {
        return message;
    }
    public static void setMessage(String message) {
        SQLInjectionControlConfiguration.message = message;
    }
}