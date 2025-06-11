package com.leep.security.hedge.access.aspect;

import com.leep.security.hedge.access.annotation.RequireRole;
import com.leep.security.hedge.access.hierarchy.RoleHierarchyService;
import com.leep.security.hedge.access.provider.UserRoleProvider;
import com.leep.security.hedge.exception.model.RoleAccessDeniedException;
import com.leep.security.hedge.tracing.dispatcher.ApiCallEventDispatcher;
import com.leep.security.hedge.tracing.model.ApiCallEvent;
import com.leep.security.hedge.tracing.model.Severity;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@Order(1)
public class RequireRoleAspect {

    @Autowired
    private UserRoleProvider roleProvider;

    @Autowired
    private ApiCallEventDispatcher dispatcher;

    @Autowired(required = false)
    private RoleHierarchyService hierarchyService;

    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint pjp, RequireRole requireRole) throws Throwable {
        Set<String> requiredRoles = Arrays.stream(requireRole.value()).collect(Collectors.toSet());
        boolean enforce = requireRole.enforce();
        boolean expandHierarchy = requireRole.expandHierarchy();

        String userId = getUserId();
        String remoteIp = getClientIp();
        Set<String> roles = roleProvider.getRolesFor(userId);

        Set<String> effectiveRoles = (expandHierarchy && hierarchyService != null)
                ? hierarchyService.expandRoles(roles)
                : roles;

        boolean hasRole = effectiveRoles.stream().anyMatch(requiredRoles::contains);

        long start = System.currentTimeMillis();
        Throwable thrown = null;

        try {
            if (!hasRole && enforce) {
                throw new RoleAccessDeniedException(userId, String.join(", ", requiredRoles));

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
            event.setUserId(userId);
            event.setRemoteIp(remoteIp);
            event.setArea("access");
            event.setDurationMillis(duration);
            event.setExceptionOccurred(!hasRole || thrown != null);
            event.setExceptionName(
                    thrown != null ? thrown.getClass().getSimpleName() :
                            (!hasRole ? "RoleAccessDeniedException" : null)
            );
            event.setSeverity(!hasRole || thrown != null ? Severity.ERROR : Severity.INFO);

            dispatcher.dispatch(event);
        }
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private String getClientIp() {
        return getRequest().getRemoteAddr();
    }

    private String getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
    }
}
