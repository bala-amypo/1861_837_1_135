package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EventUpdateRequest {

    @NotNull
    private Long eventId;

    @NotBlank
    private String updateContent;

    @NotBlank
    private String updateType; // INFO, WARNING, CRITICAL

    public EventUpdateRequest() {}

    public EventUpdateRequest(Long eventId, String updateContent, String updateType) {
        this.eventId = eventId;
        this.updateContent = updateContent;
        this.updateType = updateType;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }
}
