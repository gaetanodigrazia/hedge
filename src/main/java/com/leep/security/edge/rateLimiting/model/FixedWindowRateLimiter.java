package com.leep.security.edge.rateLimiting.model;

import com.leep.security.edge.rateLimiting.strategy.RateLimiterStrategy;
import com.leep.security.edge.time.SystemTimeProvider;
import com.leep.security.edge.time.TimeProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowRateLimiter implements RateLimiterStrategy {

    private final int limit;
    private final long windowSizeMillis;
    private final Map<String, Window> requestCounters = new ConcurrentHashMap<>();
    private final TimeProvider timeProvider;

    public FixedWindowRateLimiter(int limit, int windowSeconds) {
        this(limit, windowSeconds*1000, new SystemTimeProvider());
    }


    // solo per i test, nessun modificatore (package-private)
    FixedWindowRateLimiter(int limit, int seconds, TimeProvider timeProvider) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("Window duration must be > 0");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("Limit must be >= 0");
        }
        this.limit = limit;
        this.windowSizeMillis = seconds * 100L;
        this.timeProvider = timeProvider;
    }
    @Override
    public boolean allowRequest(String key) {

        long currentWindow = windowSizeMillis == 0
                ? timeProvider.currentTimeMillis()
                : timeProvider.currentTimeMillis() / windowSizeMillis;

        Window window = requestCounters.computeIfAbsent(key, k -> new Window(currentWindow, 0));

        synchronized (window) {
            if (window.windowStart == currentWindow) {
                if (window.count < limit) {
                    window.count++;
                    return true;
                } else {
                    return false;
                }
            } else {
                window.windowStart = currentWindow;
                window.count = 1;
                return true;
            }
        }
    }

    private static class Window {
        long windowStart;
        int count;

        Window(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
