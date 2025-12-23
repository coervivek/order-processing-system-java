package com.demo.oms.dto;

import com.demo.oms.domain.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderResponse {
    private UUID id;
    private String userId;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private Double total;
    private List<OrderItemDto> items;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
}
