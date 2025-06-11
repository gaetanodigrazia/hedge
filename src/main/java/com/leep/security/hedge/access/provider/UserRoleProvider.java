package com.leep.security.hedge.access.provider;

import java.util.Set;

public interface UserRoleProvider {
    Set<String> getRolesFor(String userId);
}
