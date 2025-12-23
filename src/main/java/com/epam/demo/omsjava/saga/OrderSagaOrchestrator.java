package com.epam.demo.omsjava.saga;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderSagaOrchestrator {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderSagaOrchestrator(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void startOrderSaga(Long orderId, Double totalAmount) {
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, totalAmount);
        kafkaTemplate.send("order-events", event);
    }

    public void compensateOrder(Long orderId) {
        kafkaTemplate.send("order-compensation", orderId);
    }
}
