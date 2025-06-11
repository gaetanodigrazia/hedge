package com.leep.security.hedge.rateLimiting;

import com.leep.security.hedge.rateLimiting.model.FixedWindowRateLimiter;
import com.leep.security.hedge.rateLimiting.strategy.RateLimiterStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestRateLimitingConfig {

    @Bean
    public RateLimiterStrategy rateLimiterStrategy() {
        return new FixedWindowRateLimiter(3, 10);
    }
}
