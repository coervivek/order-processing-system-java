package com.epam.demo.omsjava.dto;

import com.epam.demo.omsjava.domain.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void testCreateOrderRequest() {
        CreateOrderRequest request = new CreateOrderRequest();
        OrderItemDto item = new OrderItemDto();
        item.setProductName("Laptop");
        item.setQuantity(1);
        item.setPrice(999.99);
        
        request.setUserId("user123");
        request.setItems(Arrays.asList(item));
        
        assertEquals("user123", request.getUserId());
        assertEquals(1, request.getItems().size());
    }

    @Test
    void testOrderResponse() {
        OrderResponse response = new OrderResponse();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        
        OrderItemDto item = new OrderItemDto();
        item.setProductName("Laptop");
        item.setQuantity(1);
        item.setPrice(999.99);
        
        response.setId(id);
        response.setUserId("user123");
        response.setStatus(OrderStatus.PENDING);
        response.setCreatedAt(now);
        response.setTotal(999.99);
        response.setItems(Arrays.asList(item));
        
        assertEquals(id, response.getId());
        assertEquals("user123", response.getUserId());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(now, response.getCreatedAt());
        assertEquals(999.99, response.getTotal());
        assertEquals(1, response.getItems().size());
    }

    @Test
    void testOrderItemDto() {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductName("Mouse");
        dto.setQuantity(2);
        dto.setPrice(25.50);
        
        assertEquals("Mouse", dto.getProductName());
        assertEquals(2, dto.getQuantity());
        assertEquals(25.50, dto.getPrice());
    }
}
