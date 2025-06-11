package com.leep.security.hedge.access.aspect;

import com.leep.security.hedge.access.annotation.RequireRole;
import com.leep.security.hedge.access.hierarchy.RoleHierarchyService;
import com.leep.security.hedge.access.provider.UserRoleProvider;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequireRoleAspectTest {

    @Mock
    private UserRoleProvider userRoleProvider;

    @Mock
    private RoleHierarchyService roleHierarchyService;

    @InjectMocks
    private RequireRoleAspect aspect;

    @Test
    void testCheckAccess_userHasRequiredRole() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        RequireRole requireRole = mock(RequireRole.class);
        when(requireRole.value()).thenReturn(new String[] {"ROLE_ADMIN"});
        when(userRoleProvider.getRolesFor(anyString())).thenReturn(Set.of("ROLE_ADMIN"));
        when(roleHierarchyService.expandRoles(Set.of("ROLE_ADMIN")))
                .thenReturn(Set.of("ROLE_ADMIN", "ROLE_USER"));

        aspect.checkRole(joinPoint, requireRole);
        verify(joinPoint).proceed();
    }

    @Test
    void testCheckAccess_userLacksRole_throwsException() {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        RequireRole requireRole = mock(RequireRole.class);
        when(requireRole.value()).thenReturn(new String[] {"ROLE_SUPERADMIN"});
        when(userRoleProvider.getRolesFor(anyString())).thenReturn(Set.of("ROLE_USER"));
        when(roleHierarchyService.expandRoles(Set.of("ROLE_USER")))
                .thenReturn(Set.of("ROLE_USER"));

        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            aspect.checkRole(joinPoint, requireRole);
        });
    }
}
