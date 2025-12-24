package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class EventUpdate {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    private SeverityLevel severityLevel;

    @PrePersist
    public void onCreate() {
        timestamp = Instant.now();
        if (severityLevel == null) {
            severityLevel = SeverityLevel.LOW;
        }
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public Instant getTimestamp() { return timestamp; }
    public SeverityLevel getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(SeverityLevel severityLevel) { this.severityLevel = severityLevel; }
}
