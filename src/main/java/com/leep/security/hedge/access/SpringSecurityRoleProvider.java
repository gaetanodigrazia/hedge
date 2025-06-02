package com.leep.security.hedge.access;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@ConditionalOnClass(SecurityContextHolder.class)
public class SpringSecurityRoleProvider implements UserRoleProvider {

    @Override
    public Set<String> getRolesFor(String userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return Set.of();
        }

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .collect(Collectors.toSet());
    }

    @Bean
    @ConditionalOnMissingBean(UserRoleProvider.class)
    public UserRoleProvider defaultRoleProvider() {
        return userId -> Set.of();
    }
}
