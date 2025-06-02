package com.leep.security.hedge.rateLimiting.strategy;

public interface RateLimiterStrategy {
    boolean allowRequest(String key);
}
