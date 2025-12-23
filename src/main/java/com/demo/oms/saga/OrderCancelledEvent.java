package com.demo.oms.saga;

import java.util.UUID;

public class OrderCancelledEvent extends SagaEvent {
    private UUID orderId;
    private String reason;

    public OrderCancelledEvent() {
        super("ORDER_CANCELLED");
    }

    public OrderCancelledEvent(UUID orderId, String reason) {
        super("ORDER_CANCELLED");
        this.orderId = orderId;
        this.reason = reason;
    }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
