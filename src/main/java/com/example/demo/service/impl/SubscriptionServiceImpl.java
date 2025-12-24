package com.example.demo.service.impl;

import com.example.demo.entity.Subscription;
import com.example.demo.entity.User;
import com.example.demo.entity.Event;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SubscriptionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Subscription subscribe(Long userId, Long eventId) {
        if (subscriptionRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new BadRequestException("Already subscribed");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setEvent(event);
        
        return subscriptionRepository.save(sub);
    }

    @Override
    public void unsubscribe(Long userId, Long eventId) {
        Subscription sub = subscriptionRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new BadRequestException("Subscription not found")); // Test expects BadRequestException here
        subscriptionRepository.delete(sub);
    }

    @Override
    public List<Subscription> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Override
    public boolean isSubscribed(Long userId, Long eventId) {
        return subscriptionRepository.existsByUserIdAndEventId(userId, eventId);
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }
}