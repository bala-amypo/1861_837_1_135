package com.example.demo.controller;

import com.example.demo.entity.EventUpdate;
import com.example.demo.service.EventUpdateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/updates")
public class EventUpdateController {

    private final EventUpdateService eventUpdateService;

    public EventUpdateController(EventUpdateService eventUpdateService) {
        this.eventUpdateService = eventUpdateService;
    }

    @PostMapping
    public EventUpdate publishUpdate(@RequestBody EventUpdate update) {
        return eventUpdateService.publishUpdate(update);
    }

    @GetMapping("/event/{eventId}")
    public List<EventUpdate> getUpdatesForEvent(@PathVariable Long eventId) {
        return eventUpdateService.getUpdatesForEvent(eventId);
    }

    @GetMapping("/{id}")
    public EventUpdate getUpdate(@PathVariable Long id) {
        return eventUpdateService.getUpdateById(id);
    }
}