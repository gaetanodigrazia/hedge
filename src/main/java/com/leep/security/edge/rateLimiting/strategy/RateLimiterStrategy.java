package com.leep.security.edge.rateLimiting.strategy;

public interface RateLimiterStrategy {
    boolean allowRequest(String key);
}
