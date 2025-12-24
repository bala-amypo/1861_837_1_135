package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
public class Event {

    @Id @GeneratedValue
    private Long id;

    private String title;
    private String description;
    private String location;
    private String category;
    private boolean isActive = true;

    private Instant createdAt;
    private Instant lastUpdatedAt;

    @ManyToOne
    private User publisher;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
        lastUpdatedAt = Instant.now();
        isActive = true;
    }

    @PreUpdate
    public void onUpdate() {
        lastUpdatedAt = Instant.now();
    }
}
