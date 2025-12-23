package com.epam.demo.omsjava.saga;

import com.epam.demo.omsjava.saga.state.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OrderSagaOrchestrator {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SagaInstanceRepository sagaRepository;

    public OrderSagaOrchestrator(KafkaTemplate<String, Object> kafkaTemplate, 
                                 SagaInstanceRepository sagaRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.sagaRepository = sagaRepository;
    }

    @Transactional
    public void startOrderSaga(Long orderId, Double totalAmount) {
        SagaInstance saga = new SagaInstance();
        saga.setSagaId(UUID.randomUUID().toString());
        saga.setOrderId(orderId);
        saga.setStatus(SagaStatus.STARTED);
        saga.setStartedAt(LocalDateTime.now());
        sagaRepository.save(saga);
        
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, totalAmount);
        kafkaTemplate.send("order-events", String.valueOf(orderId), event);
    }

    @Transactional
    public void compensateOrder(Long orderId) {
        sagaRepository.findByOrderId(orderId).ifPresent(saga -> {
            saga.setStatus(SagaStatus.COMPENSATING);
            sagaRepository.save(saga);
        });
        
        OrderCancelledEvent event = new OrderCancelledEvent(orderId, "Customer cancellation");
        kafkaTemplate.send("order-compensation", String.valueOf(orderId), event);
    }
    
    @Transactional
    public void completeSaga(Long orderId) {
        sagaRepository.findByOrderId(orderId).ifPresent(saga -> {
            saga.setStatus(SagaStatus.COMPLETED);
            saga.setCompletedAt(LocalDateTime.now());
            sagaRepository.save(saga);
        });
    }
    
    @Transactional
    public void markCompensated(Long orderId) {
        sagaRepository.findByOrderId(orderId).ifPresent(saga -> {
            saga.setStatus(SagaStatus.COMPENSATED);
            saga.setCompletedAt(LocalDateTime.now());
            sagaRepository.save(saga);
        });
    }
}
