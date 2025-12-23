package com.epam.demo.omsjava.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CreateOrderRequest {
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemDto> items;

    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }
}
