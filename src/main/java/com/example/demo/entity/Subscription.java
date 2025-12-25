package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Subscription {

    @Id
    @GeneratedValue
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

    // GETTERS & SETTERS
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public Instant getSubscribedAt() { return subscribedAt; }
}
