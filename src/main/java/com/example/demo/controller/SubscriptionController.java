package com.example.demo.controller;

import com.example.demo.entity.Subscription;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.SubscriptionService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final JwtUtil jwtUtil;

    public SubscriptionController(SubscriptionService subscriptionService, JwtUtil jwtUtil) {
        this.subscriptionService = subscriptionService;
        this.jwtUtil = jwtUtil;
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return jwtUtil.getUserIdFromToken(token.substring(7));
        }
        throw new RuntimeException("Invalid Token");
    }

    @PostMapping("/{eventId}")
    @PreAuthorize("hasRole('SUBSCRIBER')")
    public ResponseEntity<Subscription> subscribe(@PathVariable Long eventId, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return new ResponseEntity<>(subscriptionService.subscribe(userId, eventId), HttpStatus.CREATED);
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('SUBSCRIBER')")
    public ResponseEntity<Void> unsubscribe(@PathVariable Long eventId, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        subscriptionService.unsubscribe(userId, eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Subscription>> getUserSubscriptions(@PathVariable Long userId) {
        // Validation: Admin or same user? Prompt doesn't specify, assumes protected.
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userId));
    }

    @GetMapping("/check/{userId}/{eventId}")
    public ResponseEntity<Boolean> checkSubscription(@PathVariable Long userId, @PathVariable Long eventId) {
        return ResponseEntity.ok(subscriptionService.isSubscribed(userId, eventId));
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }
}