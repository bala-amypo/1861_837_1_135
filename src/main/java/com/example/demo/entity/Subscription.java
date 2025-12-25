package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Subscription {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Event event;

    private Instant subscribedAt;

    @PrePersist
    public void onCreate() {
        subscribedAt = Instant.now();
    }

    // getters & setters
}
