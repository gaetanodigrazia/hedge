package com.leep.security.edge.rateLimiting;

public class SampleRateLimiter {
    private final int maxRequests;
    private final long durationMillis;
    private int requestCount;
    private long windowStart;

    public SampleRateLimiter(int maxRequests, int durationSeconds) {
        this.maxRequests = maxRequests;
        this.durationMillis = durationSeconds * 1000L;
        this.windowStart = System.currentTimeMillis();
        this.requestCount = 0;
    }

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        if (now - windowStart > durationMillis) {
            windowStart = now;
            requestCount = 0;
        }

        if (requestCount < maxRequests) {
            requestCount++;
            return true;
        }
        return false;
    }
}
