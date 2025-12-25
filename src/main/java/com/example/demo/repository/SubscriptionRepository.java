package com.example.demo.repository;

import com.example.demo.entity.Subscription;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository {

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    Optional<Subscription> findByUserIdAndEventId(Long userId, Long eventId);

    List<Subscription> findByUserId(Long userId);

    List<Subscription> findByEventId(Long eventId);

    Subscription save(Subscription subscription);

    void delete(Subscription subscription);
}
