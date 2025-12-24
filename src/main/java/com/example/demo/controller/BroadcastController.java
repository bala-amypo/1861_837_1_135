package com.example.demo.controller;

import com.example.demo.entity.BroadcastLog;
import com.example.demo.service.BroadcastService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/broadcasts")
public class BroadcastController {

    private final BroadcastService broadcastService;

    public BroadcastController(BroadcastService broadcastService) {
        this.broadcastService = broadcastService;
    }

    @PostMapping("/trigger/{updateId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> triggerBroadcast(@PathVariable Long updateId) {
        broadcastService.broadcastUpdate(updateId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logs/{updateId}")
    public ResponseEntity<List<BroadcastLog>> getLogsForUpdate(@PathVariable Long updateId) {
        return ResponseEntity.ok(broadcastService.getLogsForUpdate(updateId));
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BroadcastLog>> getAllLogs() {
        return ResponseEntity.ok(broadcastService.getAllLogs());
    }
}