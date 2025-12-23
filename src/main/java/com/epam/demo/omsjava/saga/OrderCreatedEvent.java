package com.epam.demo.omsjava.saga;

public class OrderCreatedEvent extends SagaEvent {
    private Long orderId;
    private Double totalAmount;

    public OrderCreatedEvent() {
        super("ORDER_CREATED");
    }

    public OrderCreatedEvent(Long orderId, Double totalAmount) {
        super("ORDER_CREATED");
        this.orderId = orderId;
        this.totalAmount = totalAmount;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
}
