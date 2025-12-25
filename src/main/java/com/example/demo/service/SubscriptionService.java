package com.example.demo.service;

import com.example.demo.entity.Subscription;
import java.util.List;

public interface SubscriptionService {

    Subscription subscribe(Long userId, Long eventId);

    void unsubscribe(Long userId, Long eventId);

    boolean isSubscribed(Long userId, Long eventId);

    List<Subscription> getUserSubscriptions(Long userId);
}
