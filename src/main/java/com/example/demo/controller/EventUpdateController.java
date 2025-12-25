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

    @GetMapping("/event/{eventId}")
    public List<EventUpdate> getUpdates(@PathVariable Long eventId) {
        return eventUpdateService.getUpdatesForEvent(eventId);
    }
}
