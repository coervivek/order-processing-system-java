package com.demo.oms.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    void testAllOrderStatuses() {
        OrderStatus[] statuses = OrderStatus.values();
        
        assertEquals(5, statuses.length);
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(OrderStatus.PROCESSING, OrderStatus.valueOf("PROCESSING"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.valueOf("SHIPPED"));
        assertEquals(OrderStatus.DELIVERED, OrderStatus.valueOf("DELIVERED"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
    }

    @Test
    void testOrderStatusValues() {
        assertEquals("PENDING", OrderStatus.PENDING.name());
        assertEquals("PROCESSING", OrderStatus.PROCESSING.name());
        assertEquals("SHIPPED", OrderStatus.SHIPPED.name());
        assertEquals("DELIVERED", OrderStatus.DELIVERED.name());
        assertEquals("CANCELLED", OrderStatus.CANCELLED.name());
    }
}
