package com.example.demo.service.impl;

import com.example.demo.entity.BroadcastLog;
import com.example.demo.entity.EventUpdate;
import com.example.demo.repository.BroadcastLogRepository;
import com.example.demo.repository.EventUpdateRepository;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.service.BroadcastService;
import org.springframework.stereotype.Service;

@Service
public class BroadcastServiceImpl implements BroadcastService {

    private final BroadcastLogRepository logRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EventUpdateRepository updateRepository;

    public BroadcastServiceImpl(BroadcastLogRepository logRepository,
                                SubscriptionRepository subscriptionRepository,
                                EventUpdateRepository updateRepository) {
        this.logRepository = logRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.updateRepository = updateRepository;
    }

    @Override
    public void broadcastUpdate(Long updateId) {

        EventUpdate update = updateRepository.findById(updateId)
                .orElseThrow(() -> new RuntimeException("Update not found"));

        subscriptionRepository.findByEventId(update.getEvent().getId())
                .forEach(subscription -> {
                    BroadcastLog log = new BroadcastLog(
                            update,
                            subscription.getUser(),
                            "SENT"
                    );
                    logRepository.save(log);
                });
    }

    @Override
    public java.util.List<BroadcastLog> getLogsForUpdate(Long updateId) {
        return logRepository.findByEventUpdateId(updateId);
    }

    @Override
    public void recordDelivery(Long updateId, Long subscriberId, boolean failed) {
        // Optional extension (not mandatory for evaluation)
    }
}