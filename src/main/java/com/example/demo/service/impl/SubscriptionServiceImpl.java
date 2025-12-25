package com.example.demo.service.impl;

import com.example.demo.entity.Event;
import com.example.demo.entity.Subscription;
import com.example.demo.entity.User;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SubscriptionService;

import java.util.List;

public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                   UserRepository userRepository,
                                   EventRepository eventRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Subscription subscribe(Long userId, Long eventId) {
        if (subscriptionRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new RuntimeException("Already subscribed");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setEvent(event);
        return subscriptionRepository.save(subscription);
    }

    @Override
    public void unsubscribe(Long userId, Long eventId) {
        Subscription subscription =
                subscriptionRepository.findByUserIdAndEventId(userId, eventId)
                        .orElseThrow(() -> new RuntimeException("Subscription not found"));
        subscriptionRepository.delete(subscription);
    }

    @Override
    public boolean isSubscribed(Long userId, Long eventId) {
        return subscriptionRepository.existsByUserIdAndEventId(userId, eventId);
    }

    @Override
    public List<Subscription> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }
}
