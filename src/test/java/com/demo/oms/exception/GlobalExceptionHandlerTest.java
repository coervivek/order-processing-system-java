package com.demo.oms.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        OrderNotFoundException exception = new OrderNotFoundException(orderId);
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleOrderNotFound(exception);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("status"));
        assertTrue(response.getBody().get("error").toString().contains(orderId.toString()));
    }

    @Test
    void handleInvalidOrderState() {
        String message = "Order can only be cancelled when in PENDING status";
        InvalidOrderStateException exception = new InvalidOrderStateException(message);
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleInvalidOrderState(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals(message, response.getBody().get("error"));
    }
}
