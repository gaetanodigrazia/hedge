package com.leep.security.hedge.injection;

import com.leep.security.hedge.exception.model.LengthControlException;
import com.leep.security.hedge.exception.model.OsInjectionDetectedException;
import com.leep.security.hedge.exception.model.SqlInjectionDetectedException;
import com.leep.security.hedge.tracing.dispatcher.ApiCallEventDispatcher;
import com.leep.security.hedge.tracing.model.ApiCallEvent;
import com.leep.security.hedge.tracing.model.Severity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Aspect
@Component
public class InjectionAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ApiCallEventDispatcher dispatcher;

    @Around("@annotation(injection)")
    public Object detectInjection(ProceedingJoinPoint pjp, Injection injection) throws Throwable {
        Map<String, String[]> paramMap = request.getParameterMap();
        Set<String> excluded = Set.of(injection.exclude());
        Throwable thrown = null;
        long start = System.currentTimeMillis();

        int maxLen = (injection.standard() != UserValidation.NONE)
                ? injection.standard().getMaxChar()
                : injection.maxChar();

        try {
            if (injection.checkAllFields()) {
                for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                    String name = entry.getKey();
                    if (excluded.contains(name)) continue;
                    for (String value : entry.getValue()) {
                        validate(value, name, injection, maxLen);
                    }
                }
            }
            return pjp.proceed();
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            long duration = System.currentTimeMillis() - start;

            ApiCallEvent event = new ApiCallEvent();
            event.setTimestamp(Instant.now().toString());
            event.setPath(getRequest().getRequestURI());
            event.setMethod(getRequest().getMethod());
            event.setUserId(getRequest().getHeader("X-User-Id"));
            event.setRemoteIp(getRequest().getRemoteAddr());
            event.setArea("injection");
            event.setDurationMillis(duration);
            event.setExceptionOccurred(thrown != null);
            event.setExceptionName(thrown != null ? thrown.getClass().getSimpleName() : null);
            event.setSeverity(thrown != null ? Severity.ERROR : Severity.INFO);

            event.setParameters(paramMap);
            event.setRequestBody(readRequestBody());

            dispatcher.dispatch(event);
        }
    }

    private void validate(String value, String field, Injection injection, int maxLen) {
        if (value == null) return;

        if (maxLen > 0 && value.length() > maxLen) {
            throw new LengthControlException("Field '" + field + "' exceeds max length: " + maxLen);
        }
        if (injection.checkSql() && isSqlInjection(value)) {
            throw new SqlInjectionDetectedException(field);
        }
        if (injection.checkOs() && isOsInjection(value)) {
            throw new OsInjectionDetectedException(field);
        }
    }

    private boolean isSqlInjection(String input) {
        String lower = input.toLowerCase();
        return lower.matches(".*([';]|--|\\b(select|update|delete|insert|drop|union|or|and)\\b).*\\s*");
    }


    private boolean isOsInjection(String input) {
        String lower = input.toLowerCase();
        return lower.matches(".*(\\||&&|;|\\$\\(|`|\\b(rm|wget|curl|nc|bash|sh)\\b).*\\s*");
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private String readRequestBody() {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] content = wrapper.getContentAsByteArray();
            return new String(content, StandardCharsets.UTF_8);
        }
        return null;
    }
}
