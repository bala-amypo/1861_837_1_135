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

    @PostMapping("/{userId}/{eventId}")
    public Subscription subscribe(@PathVariable Long userId,
                                  @PathVariable Long eventId) {
        return subscriptionService.subscribe(userId, eventId);
    }

    @DeleteMapping("/{userId}/{eventId}")
    public void unsubscribe(@PathVariable Long userId,
                            @PathVariable Long eventId) {
        subscriptionService.unsubscribe(userId, eventId);
    }

    @GetMapping("/user/{userId}")
    public List<Subscription> userSubs(@PathVariable Long userId) {
        return subscriptionService.getUserSubscriptions(userId);
    }
}
