package com.leep.security.hedge.access.provider;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SpringSecurityRoleProviderTest {

    private final SpringSecurityRoleProvider provider = new SpringSecurityRoleProvider();

    @Test
    void testGetRolesFor_noAuth_returnsEmpty() {
        SecurityContextHolder.clearContext();
        Set<String> roles = provider.getRolesFor("test");
        assertTrue(roles.isEmpty());
    }

    @Test
    void testGetRolesFor_authenticated() {
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
        );
        Authentication auth = new UsernamePasswordAuthenticationToken("test", "pass", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Set<String> roles = provider.getRolesFor("test");
        assertTrue(roles.contains("ADMIN"));
        assertTrue(roles.contains("USER"));
    }
}
