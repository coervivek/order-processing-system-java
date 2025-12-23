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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SagaTimeoutMonitorTest {

    @Mock
    private SagaInstanceRepository sagaRepository;

    @Mock
    private OrderSagaOrchestrator sagaOrchestrator;

    @InjectMocks
    private SagaTimeoutMonitor timeoutMonitor;

    private SagaInstance timedOutSaga;

    @BeforeEach
    void setUp() {
        timedOutSaga = new SagaInstance();
        timedOutSaga.setSagaId(UUID.randomUUID().toString());
        timedOutSaga.setOrderId(UUID.randomUUID().toString());
        timedOutSaga.setStatus(SagaStatus.STARTED);
        timedOutSaga.setStartedAt(LocalDateTime.now().minusMinutes(10));
    }

    @Test
    void checkSagaTimeouts_WithTimedOutSagas() {
        when(sagaRepository.findTimedOutSagas(any(LocalDateTime.class))).thenReturn(Arrays.asList(timedOutSaga));
        when(sagaRepository.save(any(SagaInstance.class))).thenReturn(timedOutSaga);
        
        timeoutMonitor.checkSagaTimeouts();
        
        verify(sagaRepository).findTimedOutSagas(any(LocalDateTime.class));
        verify(sagaRepository).save(timedOutSaga);
        verify(sagaOrchestrator).compensateOrder(any(UUID.class));
    }

    @Test
    void checkSagaTimeouts_NoTimedOutSagas() {
        when(sagaRepository.findTimedOutSagas(any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        
        timeoutMonitor.checkSagaTimeouts();
        
        verify(sagaRepository).findTimedOutSagas(any(LocalDateTime.class));
        verify(sagaRepository, never()).save(any());
        verify(sagaOrchestrator, never()).compensateOrder(any());
    }
}
