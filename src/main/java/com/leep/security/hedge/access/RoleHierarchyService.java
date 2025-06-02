package com.leep.security.hedge.access;

import java.util.*;

public class RoleHierarchyService {

    private final Map<String, Set<String>> hierarchy;

    public RoleHierarchyService(Map<String, Set<String>> hierarchy) {
        this.hierarchy = new HashMap<>(hierarchy);
    }

    public Set<String> expandRoles(Set<String> baseRoles) {
        Set<String> result = new HashSet<>(baseRoles);
        Queue<String> queue = new LinkedList<>(baseRoles);

        while (!queue.isEmpty()) {
            String role = queue.poll();
            Set<String> implied = hierarchy.getOrDefault(role, Set.of());
            for (String inherited : implied) {
                if (result.add(inherited)) {
                    queue.add(inherited);
                }
            }
        }

        return result;
    }
}
