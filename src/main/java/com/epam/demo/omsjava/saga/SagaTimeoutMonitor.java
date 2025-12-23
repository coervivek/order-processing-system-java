package com.epam.demo.omsjava.saga;

import com.epam.demo.omsjava.saga.state.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class SagaTimeoutMonitor {
    private static final Logger log = LoggerFactory.getLogger(SagaTimeoutMonitor.class);
    private static final int TIMEOUT_MINUTES = 5;
    
    private final SagaInstanceRepository sagaRepository;
    private final OrderSagaOrchestrator sagaOrchestrator;

    public SagaTimeoutMonitor(SagaInstanceRepository sagaRepository, 
                             OrderSagaOrchestrator sagaOrchestrator) {
        this.sagaRepository = sagaRepository;
        this.sagaOrchestrator = sagaOrchestrator;
    }

    @Scheduled(fixedRate = 60000) // Check every minute
    @Transactional
    public void checkSagaTimeouts() {
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES);
        List<SagaInstance> timedOutSagas = sagaRepository.findTimedOutSagas(timeout);
        
        if (!timedOutSagas.isEmpty()) {
            log.warn("Found {} timed-out SAGAs", timedOutSagas.size());
            
            for (SagaInstance saga : timedOutSagas) {
                log.error("SAGA timeout detected for OrderId: {}, SagaId: {}", 
                         saga.getOrderId(), saga.getSagaId());
                
                saga.setStatus(SagaStatus.FAILED);
                saga.setCompletedAt(LocalDateTime.now());
                sagaRepository.save(saga);
                
                // Trigger compensation
                sagaOrchestrator.compensateOrder(saga.getOrderId());
            }
        }
    }
}
