# 🌿 hedge

**Structured API Tracing, Rate Limiting & Input Protection for Spring Boot**

`hedge` is a streamlined Spring Boot library that provides Kafka-based API tracing, contextual severity classification, rate limiting, and request input enforcement to help teams meet observability and security goals.

---

## 🚀 Features

- ✅ `@ApiTrace(area = "...")`: Traces controller calls and emits structured events to Kafka
- ✅ `@RateLimited`: Enforces per-client rate limiting using Redis (fixed window)
- ✅ `@SQLInjectionControl`: Detects SQL-style and length-based anomalies in request parameters
- ✅ Dynamic severity classification (`INFO`, `WARN`, `ERROR`, `CRITICAL`)
- ✅ Easy integration with Spring Boot apps via annotations and YAML config

---

## 🧩 Use Cases

- Track and audit API usage by area, user, or exception type
- Detect injection patterns and suspicious inputs early
- Implement distributed rate limiting with alerting
- Feed structured event streams into ClickHouse, OpenSearch, or SIEMs
- Analyze request patterns for optimization or anomaly detection

---

## ⚡ Quick Start

### 1. Annotate your endpoints

```java
@GetMapping("/admin/users")
@RateLimited(limit = 10, durationSeconds = 60)
@ApiTrace(area = "admin")
@SQLInjectionControl(fields = {"username", "filter"})
public List<UserDto> getUsers(@RequestParam String username, @RequestParam String filter) {
    return userService.queryUsers(username, filter);
}
```

### 2. Add configuration

```yaml
spring:
  application:
    name: hedge

hedge:
  kafka:
    enabled: true
    area-topic-map:
      admin: admin-events
      public: public-events
      default: api-events

  redis:
    enabled: true
    host: localhost
    port: 6379
    timeout: 5000
```

---

## 🔍 Example Kafka Event

```json
{
  "path": "/admin/users",
  "method": "GET",
  "userId": "admin42",
  "remoteIp": "192.168.1.10",
  "area": "admin",
  "severity": "CRITICAL",
  "timestamp": "2025-05-27T10:15:30Z",
  "durationMillis": 124,
  "rateLimitNearThreshold": true,
  "exceptionOccurred": true,
  "exceptionName": "SQLInjectionControlException"
}
```

---

## 🛡 OWASP API Security Coverage

| OWASP API Top 10 (2023)                  | Coverage in Hedge                                                   |
|------------------------------------------|----------------------------------------------------------------------|
| API1: Broken Object Level Authorization  | ❌ Not handled — relies on external frameworks                      |
| API2: Broken Authentication              | ❌ Not in scope — expected to be handled upstream                   |
| API3: Broken Object Property Auth        | ❌ Not covered                                                       |
| API4: Unrestricted Resource Consumption  | ✅ `@RateLimited` controls overuse                                  |
| API5: Function Level Authorization       | ❌ Out of scope                                                      |
| API6: Sensitive Data Exposure            | ⚠️ Tracing highlights risky requests, but no encryption features     |
| API7: SSRF                               | ❌ Not addressed                                                     |
| API8: Security Misconfiguration          | ✅ Annotations enforce safety rules, especially for input validation |
| API9: Inventory Management               | ✅ Structured event flow enables API tracking                        |
| API10: Unsafe 3rd Party Usage            | ⚠️ `@SQLInjectionControl` mitigates unsafe input forwarded to APIs  |

### Input Protection Details

`hedge` includes:

- `@SQLInjectionControl`: annotation to guard specific parameters
- `SQLInjectionControlAspect`: Aspect that intercepts and checks fields using regex & heuristics
- `SQLInjectionControlException`, `LengthControlException`: triggered and routed to Kafka for audit

---

## 🔧 Config Reference

| Property                            | Default       | Description                                     |
|-------------------------------------|---------------|-------------------------------------------------|
| `hedge.kafka.enabled`              | `true`        | Enable Kafka integration                        |
| `hedge.kafka.area-topic-map.*`     | —             | Area-to-topic routing                           |
| `hedge.redis.enabled`              | `false`       | Enable Redis rate limiting                      |
| `hedge.redis.host`                 | `localhost`   | Redis hostname                                  |
| `hedge.redis.port`                 | `6379`        | Redis port                                      |
| `hedge.redis.timeout`              | `5000`        | Redis connection timeout in milliseconds        |

---

## 🛠 Requirements

- Java 17+
- Spring Boot 3.x
- Apache Kafka (with reachable brokers)
- Redis (optional, for rate limiting)

---

## 🛡 License

MIT License or internal license — based on your usage model.

---

## ✨ Maintainers

Built and maintained by [Your Team or Company Name].  
Contributions and suggestions are welcome!
