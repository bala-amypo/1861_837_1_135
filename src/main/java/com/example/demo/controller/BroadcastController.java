package com.example.demo.controller;

import com.example.demo.entity.BroadcastLog;
import com.example.demo.service.BroadcastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/broadcasts")
@Tag(name = "Broadcast", description = "Broadcast management endpoints")
public class BroadcastController {

    private final BroadcastService broadcastService;

    public BroadcastController(BroadcastService broadcastService) {
        this.broadcastService = broadcastService;
    }

    @PostMapping("/trigger/{updateId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Trigger broadcast (Admin)")
    public ResponseEntity<Void> triggerBroadcast(@PathVariable Long updateId) {
        broadcastService.broadcastUpdate(updateId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logs/{updateId}")
    @Operation(summary = "Get logs for update")
    public ResponseEntity<List<BroadcastLog>> getLogsForUpdate(@PathVariable Long updateId) {
        return ResponseEntity.ok(broadcastService.getLogsForUpdate(updateId));
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all broadcast logs (Admin)")
    public ResponseEntity<List<BroadcastLog>> getAllLogs() {
        return ResponseEntity.ok(broadcastService.getAllLogs());
    }
}
