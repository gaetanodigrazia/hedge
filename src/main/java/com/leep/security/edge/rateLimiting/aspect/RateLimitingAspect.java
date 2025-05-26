package com.leep.security.edge.rateLimiting.aspect;


import com.leep.security.edge.rateLimiting.SampleRateLimiter;
import com.leep.security.edge.rateLimiting.annotation.RateLimited;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitingAspect {

    private final Map<String, SampleRateLimiter> limiters = new ConcurrentHashMap<>();

    @Autowired
    private HttpServletRequest request;

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint pjp, RateLimited rateLimited) throws Throwable {
        String clientIp = getClientIp();
        String methodKey = pjp.getSignature().toShortString();

        String key = methodKey + ":" + clientIp;

        SampleRateLimiter limiter = limiters.computeIfAbsent(
                key,
                k -> new SampleRateLimiter(rateLimited.limit(), rateLimited.durationSeconds())
        );

        if (limiter.allowRequest()) {
            return pjp.proceed();
        } else {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded for IP " + clientIp);
        }
    }

    private String getClientIp() {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}

