package com.leep.security.hedge.access.hierarchy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleHierarchyServiceTest {

    private RoleHierarchyService service;

    @BeforeEach
    void setUp() {
        Map<String, Set<String>> hierarchy = new HashMap<>();
        hierarchy.put("ROLE_ADMIN", Set.of("ROLE_USER"));
        hierarchy.put("ROLE_USER", Set.of("ROLE_VIEWER"));

        service = new RoleHierarchyService(hierarchy);
    }

    @Test
    void testExpandRoles_basicHierarchy() {
        Set<String> baseRoles = Set.of("ROLE_ADMIN");
        Set<String> expected = Set.of("ROLE_ADMIN", "ROLE_USER", "ROLE_VIEWER");

        Set<String> result = service.expandRoles(baseRoles);
        assertEquals(expected, result);
    }

    @Test
    void testExpandRoles_noExpansion() {
        Set<String> baseRoles = Set.of("ROLE_VIEWER");
        Set<String> expected = Set.of("ROLE_VIEWER");

        Set<String> result = service.expandRoles(baseRoles);
        assertEquals(expected, result);
    }
}
