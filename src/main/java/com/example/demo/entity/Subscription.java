package com.example.demo.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "subscriptions",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user_id", "event_id"})
       })
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false, updatable = false)
    private Timestamp subscribedAt;

    public Subscription() {}

    public Subscription(User user, Event event) {
        this.user = user;
        this.event = event;
    }

    @PrePersist
    protected void onCreate() {
        this.subscribedAt = Timestamp.from(Instant.now());
    }

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Event getEvent() { return event; }
    public Timestamp getSubscribedAt() { return subscribedAt; }
}