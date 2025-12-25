package com.example.demo.service;

import com.example.demo.entity.BroadcastLog;
import java.util.List;

public interface BroadcastService {
    void broadcastUpdate(Long updateId);
    List<BroadcastLog> getLogsForUpdate(Long updateId);
    void recordDelivery(Long updateId, Long subscriberId, boolean successful);
    List<BroadcastLog> getAllLogs();
}