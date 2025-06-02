package com.leep.security.hedge.rateLimiting.annotation;

import com.leep.security.hedge.rateLimiting.model.enumeration.RateLimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    int limit() default 5;
    int durationSeconds() default 60;
    RateLimitType type() default RateLimitType.FIXED_WINDOW;
    boolean tracing() default true;
}
