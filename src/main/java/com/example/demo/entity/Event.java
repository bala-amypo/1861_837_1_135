package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Event {

    @Id
    @GeneratedValue
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

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public User getPublisher() { return publisher; }
    public void setPublisher(User publisher) { this.publisher = publisher; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
}
