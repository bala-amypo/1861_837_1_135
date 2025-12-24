package com.example.demo.repository;

import com.example.demo.entity.*;
import java.util.*;

public interface SubscriptionRepository {
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    Optional<Subscription> findByUserIdAndEventId(Long userId, Long eventId);
    List<Subscription> findByEventId(Long eventId);
    List<Subscription> findByUserId(Long userId);
    Subscription save(Subscription subscription);
    void delete(Subscription subscription);
}
