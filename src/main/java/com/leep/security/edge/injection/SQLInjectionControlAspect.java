package com.leep.security.edge.injection;

import com.leep.security.edge.exception.model.LengthControlException;
import com.leep.security.edge.exception.model.SQLInjectionControlException;
import com.leep.security.edge.tracing.dispatcher.ApiCallEventDispatcher;
import com.leep.security.edge.tracing.model.ApiCallEvent;
import com.leep.security.edge.tracing.model.Severity;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
public class SQLInjectionControlAspect {
    private static final Logger log = LoggerFactory.getLogger(SQLInjectionControlAspect.class);

    @Autowired
    private ApiCallEventDispatcher dispatcher;

    @Before("@annotation(sqlInjectionControl)")
    public void checkPathVariable(JoinPoint joinPoint, SQLInjectionControl sqlInjectionControl) {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            String value = String.valueOf(arg);
            try {
                checkForCommand(value);
                checkMaxLength(value, sqlInjectionControl);
                logByLevel(sqlInjectionControl, value);
            } catch (RuntimeException ex) {
                log.error("Security check failed: {}", ex.getMessage());

                ApiCallEvent event = new ApiCallEvent();
                event.setTimestamp(LocalDateTime.now().toString());
                event.setPath(getRequest().getRequestURI());
                event.setMethod(getRequest().getMethod());
                event.setUserId(getUserId());
                event.setRemoteIp(getRequest().getRemoteAddr());
                event.setArea("sql-injection");
                event.setDurationMillis(0);
                event.setExceptionOccurred(true);
                event.setExceptionName(ex.getClass().getSimpleName());
                event.setRateLimitNearThreshold(false);
                event.setSeverity(Severity.CRITICAL);

                dispatcher.dispatch(event);

                throw ex;
            }
        }
    }

    private void checkForCommand(String pathVariable) {
        String regex = "\\b(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|UNION|GRANT|REVOKE|TRUNCATE)\\b";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(pathVariable);
        if (matcher.find()) {
            throw new SQLInjectionControlException("Trovato comando SQL: " + pathVariable);
        }
    }

    private void checkMaxLength(String pathVariable, SQLInjectionControl sqlInjectionControl) {
        int max = sqlInjectionControl.customMaxChar() != -1
                ? sqlInjectionControl.customMaxChar()
                : sqlInjectionControl.maxChar().getMaxChar();

        if (pathVariable.length() > max) {
            throw new LengthControlException("Valore PathVariable max length: " + max);
        }
    }

    private void logByLevel(SQLInjectionControl control, String value) {
        switch (control.logLevel()) {
            case INFO -> log.info(value);
            case WARN -> log.warn(value);
            case DEBUG -> log.debug(value);
            case ERROR -> log.error(value);
        }
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private String getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
    }
}
