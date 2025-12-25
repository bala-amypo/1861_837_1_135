package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Event {

    @Id @GeneratedValue
    private Long id;

    private String title;
    private String description;
    private String location;
    private String category;

    @ManyToOne
    private User publisher;

    private boolean isActive = true;

    private Instant createdAt;
    private Instant lastUpdatedAt;

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

    // getters & setters
}
