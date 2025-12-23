package com.epam.demo.omsjava.saga;

import java.time.LocalDateTime;

public abstract class SagaEvent {
    private String eventId;
    private LocalDateTime timestamp;
    private String eventType;

    public SagaEvent(String eventType) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.eventType = eventType;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
}
