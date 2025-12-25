package com.example.demo.controller;

import com.example.demo.dto.EventUpdateRequest;
import com.example.demo.entity.Event;
import com.example.demo.entity.EventUpdate;
import com.example.demo.entity.UpdateType;
import com.example.demo.service.EventUpdateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/updates")
public class EventUpdateController {

    private final EventUpdateService eventUpdateService;

    public EventUpdateController(EventUpdateService eventUpdateService) {
        this.eventUpdateService = eventUpdateService;
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<EventUpdate> publishUpdate(@RequestBody EventUpdateRequest request) {
        EventUpdate update = new EventUpdate();
        Event event = new Event();
        event.setId(request.getEventId());
        update.setEvent(event);
        update.setUpdateContent(request.getUpdateContent());
        update.setUpdateType(UpdateType.valueOf(request.getUpdateType()));
        
        return new ResponseEntity<>(eventUpdateService.publishUpdate(update), HttpStatus.CREATED);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventUpdate>> getUpdatesForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventUpdateService.getUpdatesForEvent(eventId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventUpdate> getUpdate(@PathVariable Long id) {
        return ResponseEntity.of(eventUpdateService.getUpdateById(id));
    }

    @GetMapping("/")
    public ResponseEntity<List<EventUpdate>> getAllUpdates() {
        return ResponseEntity.ok(eventUpdateService.getAllUpdates());
    }
}