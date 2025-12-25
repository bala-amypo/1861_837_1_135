package com.example.demo.repository;

import com.example.demo.entity.BroadcastLog;
import java.util.List;
import java.util.Optional;

public interface BroadcastLogRepository {

    List<BroadcastLog> findByEventUpdateId(Long eventUpdateId);

    List<BroadcastLog> findBySubscriberId(Long subscriberId);

    Optional<BroadcastLog> findById(Long id);

    BroadcastLog save(BroadcastLog broadcastLog);
}
