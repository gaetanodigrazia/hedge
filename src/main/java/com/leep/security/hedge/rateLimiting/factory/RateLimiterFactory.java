package com.leep.security.hedge.rateLimiting.factory;

import com.leep.security.hedge.rateLimiting.model.FixedWindowRateLimiter;
import com.leep.security.hedge.rateLimiting.model.enumeration.RateLimitType;
import com.leep.security.hedge.rateLimiting.strategy.RateLimiterStrategy;

public class RateLimiterFactory {

    public static RateLimiterStrategy create(RateLimitType type, int limit, int windowSeconds) {
        return switch (type) {
            case FIXED_WINDOW -> new FixedWindowRateLimiter(limit, windowSeconds);
            default -> throw new IllegalArgumentException("Unsupported strategy: " + type);
        };
    }
}
