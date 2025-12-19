package com.example.demo.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "broadcast_logs")
public class BroadcastLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_update_id", nullable = false)
    private EventUpdate eventUpdate;

    @ManyToOne
    @JoinColumn(name = "subscriber_id", nullable = false)
    private User subscriber;

    @Column(nullable = false)
    private String deliveryStatus = "SENT";

    @Column(nullable = false, updatable = false)
    private Timestamp sentAt;

    public BroadcastLog() {}

    public BroadcastLog(EventUpdate eventUpdate, User subscriber, String deliveryStatus) {
        this.eventUpdate = eventUpdate;
        this.subscriber = subscriber;
        this.deliveryStatus = deliveryStatus;
    }

    @PrePersist
    protected void onCreate() {
        this.sentAt = Timestamp.from(Instant.now());
    }

    // Getters
    public Long getId() { return id; }
    public String getDeliveryStatus() { return deliveryStatus; }
    public Timestamp getSentAt() { return sentAt; }
}