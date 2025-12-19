package com.example.demo.controller;

import com.example.demo.entity.Subscription;
import com.example.demo.service.SubscriptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/{eventId}")
    public Subscription subscribe(@RequestParam Long userId,
                                  @PathVariable Long eventId) {
        return subscriptionService.subscribe(userId, eventId);
    }

    @DeleteMapping("/{eventId}")
    public String unsubscribe(@RequestParam Long userId,
                              @PathVariable Long eventId) {
        subscriptionService.unsubscribe(userId, eventId);
        return "Unsubscribed successfully";
    }

    @GetMapping("/user/{userId}")
    public List<Subscription> getUserSubscriptions(@PathVariable Long userId) {
        return subscriptionService.getSubscriptionsForUser(userId);
    }

    @GetMapping("/check/{userId}/{eventId}")
    public boolean checkSubscription(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        return subscriptionService.checkSubscription(userId, eventId);
    }
}