package com.demo.oms.saga;

import com.demo.oms.saga.state.SagaInstance;
import com.demo.oms.saga.state.SagaInstanceRepository;
import com.demo.oms.saga.state.SagaStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderSagaOrchestratorTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private SagaInstanceRepository sagaRepository;

    @InjectMocks
    private OrderSagaOrchestrator orchestrator;

    private UUID orderId;
    private SagaInstance sagaInstance;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        sagaInstance = createSagaInstance();
    }

    @Test
    void startOrderSaga_Success() {
        when(sagaRepository.save(any(SagaInstance.class))).thenReturn(sagaInstance);
        
        orchestrator.startOrderSaga(orderId, 1000.0);
        
        verify(sagaRepository).save(any(SagaInstance.class));
        verify(kafkaTemplate).send(eq("order-events"), eq(orderId.toString()), any(OrderCreatedEvent.class));
    }

    @Test
    void compensateOrder_Success() {
        when(sagaRepository.findByOrderId(orderId.toString())).thenReturn(Optional.of(sagaInstance));
        when(sagaRepository.save(any(SagaInstance.class))).thenReturn(sagaInstance);
        
        orchestrator.compensateOrder(orderId);
        
        verify(sagaRepository).findByOrderId(orderId.toString());
        verify(sagaRepository).save(sagaInstance);
        verify(kafkaTemplate).send(eq("order-compensation"), eq(orderId.toString()), any(OrderCancelledEvent.class));
    }

    @Test
    void compensateOrder_SagaNotFound() {
        when(sagaRepository.findByOrderId(orderId.toString())).thenReturn(Optional.empty());
        
        orchestrator.compensateOrder(orderId);
        
        verify(sagaRepository).findByOrderId(orderId.toString());
        verify(kafkaTemplate).send(eq("order-compensation"), eq(orderId.toString()), any(OrderCancelledEvent.class));
    }

    @Test
    void completeSaga_Success() {
        when(sagaRepository.findByOrderId(orderId.toString())).thenReturn(Optional.of(sagaInstance));
        when(sagaRepository.save(any(SagaInstance.class))).thenReturn(sagaInstance);
        
        orchestrator.completeSaga(orderId);
        
        verify(sagaRepository).findByOrderId(orderId.toString());
        verify(sagaRepository).save(sagaInstance);
    }

    @Test
    void completeSaga_NotFound() {
        when(sagaRepository.findByOrderId(orderId.toString())).thenReturn(Optional.empty());
        
        orchestrator.completeSaga(orderId);
        
        verify(sagaRepository).findByOrderId(orderId.toString());
        verify(sagaRepository, never()).save(any());
    }

    @Test
    void markCompensated_Success() {
        when(sagaRepository.findByOrderId(orderId.toString())).thenReturn(Optional.of(sagaInstance));
        when(sagaRepository.save(any(SagaInstance.class))).thenReturn(sagaInstance);
        
        orchestrator.markCompensated(orderId);
        
        verify(sagaRepository).findByOrderId(orderId.toString());
        verify(sagaRepository).save(sagaInstance);
    }

    @Test
    void markCompensated_NotFound() {
        when(sagaRepository.findByOrderId(orderId.toString())).thenReturn(Optional.empty());
        
        orchestrator.markCompensated(orderId);
        
        verify(sagaRepository).findByOrderId(orderId.toString());
        verify(sagaRepository, never()).save(any());
    }

    private SagaInstance createSagaInstance() {
        SagaInstance saga = new SagaInstance();
        saga.setSagaId(UUID.randomUUID().toString());
        saga.setOrderId(orderId.toString());
        saga.setStatus(SagaStatus.STARTED);
        saga.setStartedAt(LocalDateTime.now());
        return saga;
    }
}
