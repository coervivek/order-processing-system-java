package com.epam.demo.omsjava.saga;

import com.epam.demo.omsjava.saga.state.ProcessedEvent;
import com.epam.demo.omsjava.saga.state.ProcessedEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventListenerTest {

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @Mock
    private OrderSagaOrchestrator sagaOrchestrator;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private OrderEventListener eventListener;

    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
    }

    @Test
    void handleOrderCreatedEvent_Success() {
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, 1000.0);
        when(processedEventRepository.existsByEventId(event.getEventId())).thenReturn(false);
        when(processedEventRepository.save(any(ProcessedEvent.class))).thenReturn(new ProcessedEvent());
        
        eventListener.handleOrderCreatedEvent(event, acknowledgment);
        
        verify(processedEventRepository).existsByEventId(event.getEventId());
        verify(processedEventRepository).save(any(ProcessedEvent.class));
        verify(sagaOrchestrator).completeSaga(orderId);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void handleOrderCreatedEvent_DuplicateEvent() {
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, 1000.0);
        when(processedEventRepository.existsByEventId(event.getEventId())).thenReturn(true);
        
        eventListener.handleOrderCreatedEvent(event, acknowledgment);
        
        verify(processedEventRepository).existsByEventId(event.getEventId());
        verify(processedEventRepository, never()).save(any());
        verify(sagaOrchestrator, never()).completeSaga(any());
        verify(acknowledgment).acknowledge();
    }

    @Test
    void handleOrderCancelledEvent_Success() {
        OrderCancelledEvent event = new OrderCancelledEvent(orderId, "Customer request");
        when(processedEventRepository.existsByEventId(event.getEventId())).thenReturn(false);
        when(processedEventRepository.save(any(ProcessedEvent.class))).thenReturn(new ProcessedEvent());
        
        eventListener.handleOrderCancelledEvent(event, acknowledgment);
        
        verify(processedEventRepository).existsByEventId(event.getEventId());
        verify(processedEventRepository).save(any(ProcessedEvent.class));
        verify(sagaOrchestrator).markCompensated(orderId);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void handleOrderCancelledEvent_DuplicateEvent() {
        OrderCancelledEvent event = new OrderCancelledEvent(orderId, "Customer request");
        when(processedEventRepository.existsByEventId(event.getEventId())).thenReturn(true);
        
        eventListener.handleOrderCancelledEvent(event, acknowledgment);
        
        verify(processedEventRepository).existsByEventId(event.getEventId());
        verify(processedEventRepository, never()).save(any());
        verify(sagaOrchestrator, never()).markCompensated(any());
        verify(acknowledgment).acknowledge();
    }
}
