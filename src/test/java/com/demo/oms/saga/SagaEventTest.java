package com.demo.oms.saga;

import com.demo.oms.saga.state.ProcessedEvent;
import com.demo.oms.saga.state.SagaInstance;
import com.demo.oms.saga.state.SagaStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SagaEventTest {

    @Test
    void testOrderCreatedEvent() {
        UUID orderId = UUID.randomUUID();
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, 1000.0);
        
        assertEquals(orderId, event.getOrderId());
        assertEquals(1000.0, event.getTotalAmount());
        assertEquals("ORDER_CREATED", event.getEventType());
        assertNotNull(event.getEventId());
        assertNotNull(event.getTimestamp());
    }

    @Test
    void testOrderCreatedEventDefaultConstructor() {
        OrderCreatedEvent event = new OrderCreatedEvent();
        UUID orderId = UUID.randomUUID();
        
        event.setOrderId(orderId);
        event.setTotalAmount(500.0);
        
        assertEquals(orderId, event.getOrderId());
        assertEquals(500.0, event.getTotalAmount());
        assertEquals("ORDER_CREATED", event.getEventType());
    }

    @Test
    void testOrderCancelledEvent() {
        UUID orderId = UUID.randomUUID();
        OrderCancelledEvent event = new OrderCancelledEvent(orderId, "Customer request");
        
        assertEquals(orderId, event.getOrderId());
        assertEquals("Customer request", event.getReason());
        assertEquals("ORDER_CANCELLED", event.getEventType());
        assertNotNull(event.getEventId());
        assertNotNull(event.getTimestamp());
    }

    @Test
    void testOrderCancelledEventDefaultConstructor() {
        OrderCancelledEvent event = new OrderCancelledEvent();
        UUID orderId = UUID.randomUUID();
        
        event.setOrderId(orderId);
        event.setReason("Timeout");
        
        assertEquals(orderId, event.getOrderId());
        assertEquals("Timeout", event.getReason());
        assertEquals("ORDER_CANCELLED", event.getEventType());
    }

    @Test
    void testSagaInstance() {
        SagaInstance saga = new SagaInstance();
        String sagaId = UUID.randomUUID().toString();
        String orderId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        
        saga.setSagaId(sagaId);
        saga.setOrderId(orderId);
        saga.setStatus(SagaStatus.STARTED);
        saga.setStartedAt(now);
        saga.setCompletedAt(now.plusMinutes(5));
        
        assertEquals(sagaId, saga.getSagaId());
        assertEquals(orderId, saga.getOrderId());
        assertEquals(SagaStatus.STARTED, saga.getStatus());
        assertEquals(now, saga.getStartedAt());
        assertNotNull(saga.getCompletedAt());
    }

    @Test
    void testProcessedEvent() {
        ProcessedEvent event = new ProcessedEvent();
        String eventId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        
        event.setEventId(eventId);
        event.setEventType("ORDER_CREATED");
        event.setProcessedAt(now);
        
        assertEquals(eventId, event.getEventId());
        assertEquals("ORDER_CREATED", event.getEventType());
        assertEquals(now, event.getProcessedAt());
    }
}
