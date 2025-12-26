package com.example.demo.controller;

import com.example.demo.dto.EventUpdateRequest;
import com.example.demo.entity.Event;
import com.example.demo.entity.EventUpdate;
import com.example.demo.entity.UpdateType;
import com.example.demo.service.BroadcastService;
import com.example.demo.service.EventUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/updates")
@Tag(name = "EventUpdate", description = "Event update endpoints")
public class EventUpdateController {

    private final EventUpdateService eventUpdateService;
    private final BroadcastService broadcastService;

    public EventUpdateController(EventUpdateService eventUpdateService, BroadcastService broadcastService) {
        this.eventUpdateService = eventUpdateService;
        this.broadcastService = broadcastService;
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('PUBLISHER')")
    @Operation(summary = "Publish event update")
    public ResponseEntity<EventUpdate> publishUpdate(@Valid @RequestBody EventUpdateRequest request) {
        EventUpdate update = new EventUpdate();
        Event event = new Event();
        event.setId(request.getEventId());
        update.setEvent(event);
        update.setUpdateContent(request.getUpdateContent());
        try {
            update.setUpdateType(UpdateType.valueOf(request.getUpdateType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            update.setUpdateType(UpdateType.INFO);
        }

        EventUpdate saved = eventUpdateService.publishUpdate(update);
        
        // Trigger broadcast explicitly here since service was constrained
        broadcastService.broadcastUpdate(saved.getId());

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get updates for event")
    public ResponseEntity<List<EventUpdate>> getUpdatesForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventUpdateService.getUpdatesForEvent(eventId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get update by ID")
    public ResponseEntity<EventUpdate> getUpdateById(@PathVariable Long id) {
        return ResponseEntity.of(eventUpdateService.getUpdateById(id));
    }

    @GetMapping("/")
    @Operation(summary = "Get all updates")
    public ResponseEntity<List<EventUpdate>> getAllUpdates() {
        return ResponseEntity.ok(eventUpdateService.getAllUpdates());
    }
}
