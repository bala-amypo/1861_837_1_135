package com.example.demo.repository;

import com.example.demo.entity.BroadcastLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BroadcastLogRepository extends JpaRepository<BroadcastLog, Long> {

    List<BroadcastLog> findByEventUpdateId(Long eventUpdateId);

    List<BroadcastLog> findBySubscriberId(Long subscriberId);
}
