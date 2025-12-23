package com.epam.demo.omsjava.saga;

public class OrderCancelledEvent extends SagaEvent {
    private Long orderId;
    private String reason;

    public OrderCancelledEvent() {
        super("ORDER_CANCELLED");
    }

    public OrderCancelledEvent(Long orderId, String reason) {
        super("ORDER_CANCELLED");
        this.orderId = orderId;
        this.reason = reason;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
