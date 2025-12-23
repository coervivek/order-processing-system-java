package com.demo.oms.saga.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SagaStatusTest {

    @Test
    void testAllSagaStatuses() {
        SagaStatus[] statuses = SagaStatus.values();
        
        assertEquals(5, statuses.length);
        assertEquals(SagaStatus.STARTED, SagaStatus.valueOf("STARTED"));
        assertEquals(SagaStatus.COMPLETED, SagaStatus.valueOf("COMPLETED"));
        assertEquals(SagaStatus.COMPENSATING, SagaStatus.valueOf("COMPENSATING"));
        assertEquals(SagaStatus.COMPENSATED, SagaStatus.valueOf("COMPENSATED"));
        assertEquals(SagaStatus.FAILED, SagaStatus.valueOf("FAILED"));
    }

    @Test
    void testSagaStatusValues() {
        assertEquals("STARTED", SagaStatus.STARTED.name());
        assertEquals("COMPLETED", SagaStatus.COMPLETED.name());
        assertEquals("COMPENSATING", SagaStatus.COMPENSATING.name());
        assertEquals("COMPENSATED", SagaStatus.COMPENSATED.name());
        assertEquals("FAILED", SagaStatus.FAILED.name());
    }
}
