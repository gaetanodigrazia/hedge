package com.leep.security.edge.rateLimiting.model.enumeration;

public enum RateLimitType {
    FIXED_WINDOW,
    SLIDING_WINDOW,
    TOKEN_BUCKET,
    LEAKY_BUCKET,
    CONCURRENCY_LIMIT
}
