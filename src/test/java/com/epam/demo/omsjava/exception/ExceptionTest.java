package com.epam.demo.omsjava.exception;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void orderNotFoundException_Message() {
        UUID orderId = UUID.randomUUID();
        OrderNotFoundException exception = new OrderNotFoundException(orderId);
        
        assertTrue(exception.getMessage().contains(orderId.toString()));
        assertTrue(exception.getMessage().contains("Order not found"));
    }

    @Test
    void invalidOrderStateException_Message() {
        String message = "Invalid state transition";
        InvalidOrderStateException exception = new InvalidOrderStateException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void orderNotFoundException_IsRuntimeException() {
        UUID orderId = UUID.randomUUID();
        OrderNotFoundException exception = new OrderNotFoundException(orderId);
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void invalidOrderStateException_IsRuntimeException() {
        InvalidOrderStateException exception = new InvalidOrderStateException("test");
        
        assertTrue(exception instanceof RuntimeException);
    }
}
