package com.example.demo.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "event_updates")
public class EventUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false, length = 1000)
    private String updateContent;

    @Column(nullable = false)
    private String updateType;

    @Column(nullable = false, updatable = false)
    private Timestamp postedAt;

    @OneToMany(mappedBy = "eventUpdate")
    private List<BroadcastLog> broadcastLogs;

    // ✅ No-arg constructor
    public EventUpdate() {
    }

    // ✅ Parameterized constructor
    public EventUpdate(Event event, String updateContent, String updateType) {
        this.event = event;
        this.updateContent = updateContent;
        this.updateType = updateType;
    }

    // ✅ Auto timestamp
    @PrePersist
    protected void onCreate() {
        this.postedAt = Timestamp.from(Instant.now());
    }

    // =====================
    // Getters and Setters
    // =====================

    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    // 🔥 THIS SETTER FIXES YOUR BUILD ERROR
    public void setEvent(Event event) {
        this.event = event;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public Timestamp getPostedAt() {
        return postedAt;
    }
}