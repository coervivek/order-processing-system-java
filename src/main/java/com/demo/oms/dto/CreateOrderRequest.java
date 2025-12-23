package com.demo.oms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class CreateOrderRequest {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemDto> items;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
}
