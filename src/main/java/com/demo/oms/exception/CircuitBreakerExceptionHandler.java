package com.demo.oms.exception;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CircuitBreakerExceptionHandler {

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<Map<String, String>> handleCircuitBreakerException(CallNotPermittedException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Service temporarily unavailable. Circuit breaker is open."));
    }
}
