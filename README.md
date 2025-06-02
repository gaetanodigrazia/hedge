# Hedge

**Secure & Observable API Behavior for Spring Boot**

`hedge` is a non-invasive Spring Boot library that adds structured Kafka-based API tracing, rate limiting, role-based access control, and input protection — all powered by annotations, and extensible via interfaces.

---

## 🚀 Features

- ✅ `@RequireRole`: Role-based access guard for controller methods
- ✅ `@ApiTrace`: Emits structured request metadata with contextual severity
- ✅ `@RateLimited`: Redis-backed fixed-window rate limiting per IP or user
- ✅ `@SQLInjectionControl`: Validates request parameters against SQL-like patterns
- ✅ Kafka-native event dispatching (area/topic mapped)
- ✅ Optional role hierarchy expansion (`RoleHierarchyService`)
- ✅ Redis-backed trace stream for dashboards
- ✅ Plug-and-play: no changes to controller logic needed

---

## ⚡ Quick Start

### 1. Annotate Your Controller

```java
@GetMapping("/admin/users")
@ApiTrace(area = "admin")
@RateLimited(limit = 20, durationSeconds = 60)
@SQLInjectionControl(fields = {"username"})
@RequireRole(value = {"ADMIN"})
public List<UserDto> getUsers(@RequestParam String username) {
    return userService.findByUsername(username);
}
```

### 2. Configure `application.yml`

```yaml
spring:
  application:
    name: hedge

hedge:
  kafka:
    enabled: true
    area-topic-map:
      admin: hedge.admin.topic
      user: hedge.user.topic
      default: hedge.default.topic

  redis:
    enabled: true
    host: localhost
    port: 6379
```

---

## 🔐 Role-Based Access Control

Use `@RequireRole` to restrict controller access by user roles:

```java
@RequireRole(value = {"ADMIN", "SUPPORT"})
@GetMapping("/secure/stats")
public StatsData stats() { ... }
```

### ✅ Flexible Role Handling

- Role resolution is delegated via `UserRoleProvider`
- Optional `RoleHierarchyService` allows hierarchical access (e.g., ADMIN ⇒ USER)
- Opt-out per method via `expandHierarchy = false`

### Spring Security Example

```java
@Component
public class SpringSecurityRoleProvider implements UserRoleProvider {
    public Set<String> getRolesFor(String userId) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(r -> r.replace("ROLE_", ""))
            .collect(Collectors.toSet());
    }
}
```

### Role Hierarchy Example

```java
@Component
public class CustomRoleHierarchyService implements RoleHierarchyService {

    private static final Map<String, Set<String>> ROLE_TREE = Map.of(
        "ADMIN", Set.of("USER", "AUDITOR"),
        "SUPERVISOR", Set.of("USER")
    );

    @Override
    public Set<String> expandRoles(Set<String> roles) {
        Set<String> expanded = new HashSet<>(roles);
        for (String role : roles) {
            expanded.addAll(ROLE_TREE.getOrDefault(role, Set.of()));
        }
        return expanded;
    }
}
```

---

## 🔍 Kafka Event Payload Example

```json
{
  "path": "/admin/users",
  "method": "GET",
  "userId": "admin42",
  "remoteIp": "192.168.1.10",
  "area": "admin",
  "severity": "CRITICAL",
  "timestamp": "2025-06-02T12:00:00Z",
  "rateLimitNearThreshold": true,
  "exceptionOccurred": true,
  "exceptionName": "SQLInjectionControlException"
}
```

---

## 🛡 OWASP API Security Coverage

| OWASP API Top 10 (2023) | Coverage in Hedge |
|--------------------------|--------------------|
| API1: Broken Object Auth | ❌ Not handled      |
| API2: Broken Auth        | ❌ Not in scope     |
| API3: Prop Auth          | ❌ Not handled      |
| API4: Resource Abuse     | ✅ `@RateLimited`   |
| API5: Func Auth          | ✅ `@RequireRole`   |
| API6: Sensitive Data     | ⚠️ Tracing only     |
| API7: SSRF               | ❌ Not handled      |
| API8: Misconfiguration   | ✅ Enforced via annotations |
| API9: Inventory          | ✅ Kafka trace      |
| API10: Unsafe APIs       | ⚠️ `@SQLInjectionControl` |

---

## 📦 Configuration Reference

| Property                        | Description                              |
|----------------------------------|------------------------------------------|
| `hedge.kafka.enabled`           | Enable Kafka dispatching                 |
| `hedge.kafka.area-topic-map`    | Map area tags to topics                  |
| `hedge.redis.enabled`           | Enable Redis-based rate limiting         |
| `hedge.redis.host`              | Redis hostname                           |
| `hedge.redis.port`              | Redis port                               |

---

## 🧰 Redis Support for Dashboards

`hedge` can dispatch structured API events to Redis as well:

```java
@Component
@ConditionalOnProperty(name = "redis.enabled", havingValue = "true")
public class RedisApiCallDispatcher implements ApiCallEventDispatcher {
    public void dispatch(ApiCallEvent event) {
        // pushes event to api:traces list
    }
}
```

Use `CompositeApiCallDispatcher` to send to both Redis and Kafka.

---


## 🧰 Requirements

- Java 17+
- Spring Boot 3.x
- Apache Kafka (for event streaming)
- Redis (optional, for rate limiting and dashboard)

---

## 🛡 License

MIT License

Copyright (c) 2025 Gaetano Di Grazia

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

---

## ✨ Maintainer

Gaetano Di Grazia
