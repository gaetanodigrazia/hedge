package com.leep.security.hedge.exception.model;

public class RoleAccessDeniedException extends RuntimeException {
    public RoleAccessDeniedException(String userId, String requiredRole) {
        super("User '" + userId + "' does not have required role: " + requiredRole);
    }
}