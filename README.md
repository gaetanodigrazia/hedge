# hedge-core

**Modular API Observability & Rate Limiting for Spring Boot**

🚀 `hedge-core` is a drop-in library that brings structured API tracing, dynamic severity classification, and flexible rate limiting to your Spring Boot services. Kafka-native, annotation-driven, and non-invasive.

---

## 📦 Features

- ✅ `@RateLimited`: Fixed window rate limiter with per-client key
- ✅ `@ApiTrace(area = "...")`: Traces API calls to Kafka, per-area routing
- ✅ Dynamic severity (`INFO`, `WARN`, `ERROR`, `CRITICAL`) based on context
- ✅ Kafka topic routing configurable via `application.yml`
- ✅ Minimal setup, fully pluggable, no controller changes

---

## 🧩 Use Cases

- Security audit and real-time traceability
- Distributed rate limiting trigger feeds
- Routing structured events to observability stacks (Kafka → ClickHouse, Elastic, S3, etc.)
- API behavior analysis and anomaly detection

---

## 🚀 Quick Start

### 1. Annotate your controllers

```java
@GetMapping("/admin/users")
@RateLimited(limit = 10, durationSeconds = 60)
@ApiTrace(area = "admin")
public List<UserDto> getAllUsers() {
    ...
}
```

### 2. Configure application.yml

```yaml
spring:
  application:
    name: hedge

kafka:
  enabled: true
  area-topic-map:
    admin: admin-events
    user: user-events
    public: public-events
    default: api-call-events
```

---

## 🔍 Kafka Payload Example

```json
{
  "path": "/admin/users",
  "method": "GET",
  "userId": "admin42",
  "remoteIp": "192.168.1.10",
  "area": "admin",
  "severity": "CRITICAL",
  "timestamp": "2025-05-27T10:15:30",
  "durationMillis": 124,
  "rateLimitNearThreshold": true,
  "exceptionOccurred": false
}
```

---

## 🔧 Configuration Options

| Property                    | Default            | Description                              |
|-----------------------------|--------------------|------------------------------------------|
| `kafka.enabled`             | `true`             | Enable/disable Kafka event publishing    |
| `kafka.area-topic-map.*`   |                    | Maps logical areas to Kafka topics       |

---

## 📁 Modules (if split)

- `hedge-core` – annotations, interfaces, base logic
- `hedge-kafka` – Kafka integration & producer
- `hedge-limiter` – Fixed window limiter
- `hedge-observability` – `@ApiTrace` and tracing aspect

---

## 🛡 License

MIT or internal license (insert here)

---

## ✨ Maintainer

Built by [Your Name or Team] · [company/team/email] · Contributions welcome!
