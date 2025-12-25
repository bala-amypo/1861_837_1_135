package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class User {

    @Id @GeneratedValue
    private Long id;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        if (this.role == null) {
            this.role = Role.SUBSCRIBER;
        }
    }

    // getters & setters
}
