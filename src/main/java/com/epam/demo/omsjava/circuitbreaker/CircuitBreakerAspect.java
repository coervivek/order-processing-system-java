package com.epam.demo.omsjava.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CircuitBreakerAspect {

    private final CircuitBreakerRegistry registry;

    public CircuitBreakerAspect(CircuitBreakerRegistry registry) {
        this.registry = registry;
    }

    @Around("@annotation(withCircuitBreaker)")
    public Object applyCircuitBreaker(ProceedingJoinPoint joinPoint, WithCircuitBreaker withCircuitBreaker) throws Throwable {
        CircuitBreaker circuitBreaker = registry.circuitBreaker(withCircuitBreaker.name());
        return circuitBreaker.executeSupplier(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
}
