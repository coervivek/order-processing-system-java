package com.demo.oms.saga;

import java.util.UUID;

public class OrderCreatedEvent extends SagaEvent {
    private UUID orderId;
    private Double totalAmount;

    public OrderCreatedEvent() {
        super("ORDER_CREATED");
    }

    public OrderCreatedEvent(UUID orderId, Double totalAmount) {
        super("ORDER_CREATED");
        this.orderId = orderId;
        this.totalAmount = totalAmount;
    }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
}
