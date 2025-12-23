package com.epam.demo.omsjava.saga.state;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "saga_instance")
public class SagaInstance {
    @Id
    private String sagaId;
    
    private Long orderId;
    
    @Enumerated(EnumType.STRING)
    private SagaStatus status;
    
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    
    public String getSagaId() { return sagaId; }
    public void setSagaId(String sagaId) { this.sagaId = sagaId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public SagaStatus getStatus() { return status; }
    public void setStatus(SagaStatus status) { this.status = status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
