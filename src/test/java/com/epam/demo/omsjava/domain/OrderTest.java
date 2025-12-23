package com.epam.demo.omsjava.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testOrderGettersAndSetters() {
        Order order = new Order();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        
        order.setId(id);
        order.setUserId("user123");
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(now);
        
        OrderItem item = new OrderItem();
        item.setProductName("Laptop");
        item.setQuantity(1);
        item.setPrice(999.99);
        order.setItems(Arrays.asList(item));
        
        assertEquals(id, order.getId());
        assertEquals("user123", order.getUserId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(now, order.getCreatedAt());
        assertEquals(1, order.getItems().size());
    }
}
