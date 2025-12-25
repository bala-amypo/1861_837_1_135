package com.example.demo.service;

import com.example.demo.entity.BroadcastLog;
import java.util.List;

public interface BroadcastService {
    void broadcastUpdate(Long updateId);
    void recordDelivery(Long updateId, Long subscriberId, boolean success);
    List<BroadcastLog> getLogsForUpdate(Long updateId);
}
