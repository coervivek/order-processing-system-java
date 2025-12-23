package com.demo.oms.saga;

import com.demo.oms.saga.state.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
public class OrderEventListener {
    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    
    private final ProcessedEventRepository processedEventRepository;
    private final OrderSagaOrchestrator sagaOrchestrator;

    public OrderEventListener(ProcessedEventRepository processedEventRepository,
                             OrderSagaOrchestrator sagaOrchestrator) {
        this.processedEventRepository = processedEventRepository;
        this.sagaOrchestrator = sagaOrchestrator;
    }

    @KafkaListener(topics = "order-events", groupId = "oms-group")
    @Transactional
    public void handleOrderCreatedEvent(OrderCreatedEvent event, Acknowledgment ack) {
        if (processedEventRepository.existsByEventId(event.getEventId())) {
            log.warn("Duplicate event detected, skipping: {}", event.getEventId());
            ack.acknowledge();
            return;
        }
        
        log.info("SAGA: Processing OrderCreatedEvent - OrderId: {}, Amount: {}, EventId: {}", 
                 event.getOrderId(), event.getTotalAmount(), event.getEventId());
        
        // Simulate downstream service calls
        // In real microservices: call Payment, Inventory, Shipping services
        
        ProcessedEvent processed = new ProcessedEvent();
        processed.setEventId(event.getEventId());
        processed.setEventType(event.getEventType());
        processed.setProcessedAt(LocalDateTime.now());
        processedEventRepository.save(processed);
        
        sagaOrchestrator.completeSaga(event.getOrderId());
        ack.acknowledge();
        
        log.info("SAGA: OrderCreatedEvent processed successfully for OrderId: {}", event.getOrderId());
    }

    @KafkaListener(topics = "order-compensation", groupId = "oms-group")
    @Transactional
    public void handleOrderCancelledEvent(OrderCancelledEvent event, Acknowledgment ack) {
        if (processedEventRepository.existsByEventId(event.getEventId())) {
            log.warn("Duplicate compensation event detected, skipping: {}", event.getEventId());
            ack.acknowledge();
            return;
        }
        
        log.info("SAGA COMPENSATION: Processing OrderCancelledEvent - OrderId: {}, Reason: {}, EventId: {}", 
                 event.getOrderId(), event.getReason(), event.getEventId());
        
        // Simulate compensation in downstream services
        // In real microservices: refund payment, release inventory, cancel shipment
        
        ProcessedEvent processed = new ProcessedEvent();
        processed.setEventId(event.getEventId());
        processed.setEventType(event.getEventType());
        processed.setProcessedAt(LocalDateTime.now());
        processedEventRepository.save(processed);
        
        sagaOrchestrator.markCompensated(event.getOrderId());
        ack.acknowledge();
        
        log.info("SAGA COMPENSATION: OrderCancelledEvent processed successfully for OrderId: {}", event.getOrderId());
    }
}
