package com.leep.security.edge.rateLimiting.aspect;

import com.leep.security.edge.exception.model.RateLimitExceededException;
import com.leep.security.edge.rateLimiting.annotation.RateLimited;
import com.leep.security.edge.rateLimiting.factory.RateLimiterFactory;
import com.leep.security.edge.rateLimiting.strategy.RateLimiterStrategy;
import com.leep.security.edge.tracing.dispatcher.ApiCallEventDispatcher;
import com.leep.security.edge.tracing.model.ApiCallEvent;
import com.leep.security.edge.tracing.model.Severity;
import com.leep.security.edge.tracing.service.SeverityEvaluator;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitingAspect {

    private final Map<String, RateLimiterStrategy> limiters = new ConcurrentHashMap<>();

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ApiCallEventDispatcher dispatcher;

    @Autowired
    private SeverityEvaluator evaluator;

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint pjp, RateLimited rateLimited) throws Throwable {
        String clientIp = getClientIp();
        String methodKey = pjp.getSignature().toShortString();
        String key = methodKey + ":" + clientIp;

        RateLimiterStrategy limiter = limiters.computeIfAbsent(
                key,
                k -> RateLimiterFactory.create(rateLimited.type(), rateLimited.limit(), rateLimited.durationSeconds())
        );

        boolean allowed = limiter.allowRequest(key);
        long start = System.currentTimeMillis();
        Throwable thrown = null;

        try {
            if (!allowed) {
                throw new RateLimitExceededException("Rate limit exceeded for client: " + clientIp);
            }
            return pjp.proceed();
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            if (rateLimited.tracing()) {
                long duration = System.currentTimeMillis() - start;

                ApiCallEvent event = new ApiCallEvent();
                event.setTimestamp(LocalDateTime.now().toString());
                event.setPath(getRequest().getRequestURI());
                event.setMethod(getRequest().getMethod());
                event.setUserId(getUserId());
                event.setRemoteIp(request.getRemoteAddr());
                event.setArea("rate-limit");
                event.setDurationMillis(duration);
                event.setExceptionOccurred(thrown != null || !allowed);
                event.setExceptionName(
                        thrown != null ? thrown.getClass().getSimpleName() :
                                (!allowed ? RateLimitExceededException.class.getSimpleName() : null)
                );
                event.setRateLimitNearThreshold(!allowed);
                event.setRateLimiterType(rateLimited.type().name());

                Severity severity = evaluator.evaluate("rate-limit", !allowed, thrown != null || !allowed);
                event.setSeverity(severity);

                dispatcher.dispatch(event);
            }
        }
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private String getClientIp() {
        return request.getRemoteAddr();
    }

    private String getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
    }
}

