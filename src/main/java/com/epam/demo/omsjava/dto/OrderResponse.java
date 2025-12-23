package com.epam.demo.omsjava.dto;

import com.epam.demo.omsjava.domain.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
}
