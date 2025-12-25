package com.example.demo.repository;

import com.example.demo.entity.BroadcastLog;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BroadcastLogRepository {

    List<BroadcastLog> findByEventUpdateId(Long eventUpdateId);

    List<BroadcastLog> findBySubscriberId(Long subscriberId);

    Optional<BroadcastLog> findById(Long id);

    BroadcastLog save(BroadcastLog broadcastLog);
}
