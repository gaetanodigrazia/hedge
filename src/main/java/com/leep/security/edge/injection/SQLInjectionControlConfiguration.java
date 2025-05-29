package com.leep.security.edge.injection;

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