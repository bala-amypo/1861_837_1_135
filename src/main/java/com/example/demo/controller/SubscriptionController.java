package com.example.demo.controller;

import com.example.demo.entity.Subscription;
import com.example.demo.entity.User;
import com.example.demo.service.SubscriptionService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@Tag(name = "Subscription", description = "Subscription management endpoints")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserService userService;

    public SubscriptionController(SubscriptionService subscriptionService, UserService userService) {
        this.subscriptionService = subscriptionService;
        this.userService = userService;
    }

    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email).map(User::getId).orElseThrow();
    }

    @PostMapping("/{eventId}")
    @PreAuthorize("hasAuthority('SUBSCRIBER')")
    @Operation(summary = "Subscribe to event")
    public ResponseEntity<Subscription> subscribe(@PathVariable Long eventId, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        return new ResponseEntity<>(subscriptionService.subscribe(userId, eventId), HttpStatus.CREATED);
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasAuthority('SUBSCRIBER')")
    @Operation(summary = "Unsubscribe from event")
    public ResponseEntity<Void> unsubscribe(@PathVariable Long eventId, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        subscriptionService.unsubscribe(userId, eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user subscriptions")
    public ResponseEntity<List<Subscription>> getUserSubscriptions(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userId));
    }

    @GetMapping("/check/{userId}/{eventId}")
    @Operation(summary = "Check subscription status")
    public ResponseEntity<Boolean> isSubscribed(@PathVariable Long userId, @PathVariable Long eventId) {
        return ResponseEntity.ok(subscriptionService.isSubscribed(userId, eventId));
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all subscriptions (Admin)")
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }
}
