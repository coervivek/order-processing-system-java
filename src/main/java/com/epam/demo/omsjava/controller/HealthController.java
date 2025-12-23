package com.epam.demo.omsjava.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final CircuitBreakerRegistry registry;

    public HealthController(CircuitBreakerRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/circuit-breaker")
    public Map<String, Object> getCircuitBreakerStatus() {
        CircuitBreaker cb = registry.circuitBreaker("orderService");
        return Map.of(
                "name", cb.getName(),
                "state", cb.getState().toString(),
                "metrics", Map.of(
                        "failureRate", cb.getMetrics().getFailureRate(),
                        "numberOfSuccessfulCalls", cb.getMetrics().getNumberOfSuccessfulCalls(),
                        "numberOfFailedCalls", cb.getMetrics().getNumberOfFailedCalls()
                )
        );
    }
}
