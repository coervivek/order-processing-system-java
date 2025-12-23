package com.demo.oms.saga.state;

public enum SagaStatus {
    STARTED,
    COMPLETED,
    COMPENSATING,
    COMPENSATED,
    FAILED
}
