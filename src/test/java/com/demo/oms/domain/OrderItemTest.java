package com.demo.oms.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void testOrderItemGettersAndSetters() {
        OrderItem item = new OrderItem();
        UUID id = UUID.randomUUID();
        Order order = new Order();
        
        item.setId(id);
        item.setProductName("Laptop");
        item.setQuantity(2);
        item.setPrice(999.99);
        item.setOrder(order);
        
        assertEquals(id, item.getId());
        assertEquals("Laptop", item.getProductName());
        assertEquals(2, item.getQuantity());
        assertEquals(999.99, item.getPrice());
        assertEquals(order, item.getOrder());
    }
}
