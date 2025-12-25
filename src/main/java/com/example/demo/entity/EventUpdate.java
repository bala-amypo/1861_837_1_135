package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class EventUpdate {

    @Id @GeneratedValue
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
}
