# Rate Limiting and Circuit Breaker Implementation

## Overview

This implementation adds two critical resilience patterns to the OMS system:
- **Rate Limiting**: Token bucket algorithm using Bucket4j (100 requests/minute per user/IP)
- **Circuit Breaker**: Resilience4j with 50% failure threshold and 30s recovery time

## Rate Limiting

### Implementation
- **Algorithm**: Token Bucket (Bucket4j)
- **Limit**: 100 requests per minute per user/IP
- **Key**: X-User-Id header or IP address
- **Response**: HTTP 429 (Too Many Requests)

### Configuration
```properties
ratelimit.enabled=true
ratelimit.capacity=100
ratelimit.refill-tokens=100
ratelimit.refill-duration-minutes=1
```

### Testing
```bash
# Normal request
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user123" \
  -d '{"userId":"user123","items":[{"productName":"Test","quantity":1,"price":10.0}]}'

# Exceed rate limit (run 101+ times within 1 minute)
for i in {1..101}; do curl http://localhost:8080/api/orders; done
# Response: {"error":"Rate limit exceeded"}
```

## Circuit Breaker

### Implementation
- **Library**: Resilience4j
- **Failure Threshold**: 50% (opens after 50% failures)
- **Sliding Window**: 10 calls
- **Minimum Calls**: 5 (before calculating failure rate)
- **Wait Duration**: 30 seconds (before attempting recovery)

### Configuration
```properties
resilience4j.circuitbreaker.instances.orderService.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.orderService.wait-duration-in-open-state=30s
resilience4j.circuitbreaker.instances.orderService.sliding-window-size=10
resilience4j.circuitbreaker.instances.orderService.minimum-number-of-calls=5
```

### States
1. **CLOSED**: Normal operation, requests pass through
2. **OPEN**: Circuit tripped, requests fail immediately (503 Service Unavailable)
3. **HALF_OPEN**: Testing recovery, limited requests allowed

### Usage
```java
@WithCircuitBreaker(name = "orderService")
public OrderResponse createOrder(CreateOrderRequest request) {
    // Protected method
}
```

### Monitoring
```bash
# Check circuit breaker status
curl http://localhost:8080/api/health/circuit-breaker

# Response:
{
  "name": "orderService",
  "state": "CLOSED",
  "metrics": {
    "failureRate": 0.0,
    "numberOfSuccessfulCalls": 10,
    "numberOfFailedCalls": 0
  }
}
```

## Architecture

```
Client Request
     ↓
RateLimitFilter (100 req/min)
     ↓
OrderController
     ↓
CircuitBreakerAspect (@WithCircuitBreaker)
     ↓
OrderService (createOrder, getOrder)
     ↓
Database/Kafka
```

## Components

### Rate Limiting
- `RateLimitFilter`: Servlet filter for request throttling
- Token bucket per user/IP with in-memory cache

### Circuit Breaker
- `CircuitBreakerConfiguration`: Resilience4j setup
- `CircuitBreakerAspect`: AOP interceptor
- `WithCircuitBreaker`: Custom annotation
- `CircuitBreakerExceptionHandler`: 503 error handling
- `HealthController`: Status monitoring endpoint

## Dependencies
```gradle
implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")
implementation("com.bucket4j:bucket4j-core:8.10.1")
```

## Benefits

### Rate Limiting
✅ Prevents API abuse and DDoS attacks
✅ Fair resource allocation per user
✅ Protects backend services from overload

### Circuit Breaker
✅ Prevents cascading failures
✅ Fast failure detection (no waiting for timeouts)
✅ Automatic recovery testing
✅ System stability under partial failures
