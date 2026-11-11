package com.quori.event.controller;

import com.quori.event.dto.CreateEventRequest;
import com.quori.event.dto.EventResponse;
import com.quori.event.dto.UpdateEventStatusRequest;
import com.quori.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping("/organizations/{orgId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ORGANIZER') and @authz.isOrganizationOwner(principal, #orgId)")
    @Operation(summary = "Create an event for an organization")
    public EventResponse create(@PathVariable UUID orgId,
                                @Valid @RequestBody CreateEventRequest request) {
        return service.create(orgId, request);
    }

    @GetMapping("/organizations/{orgId}/events")
    @Operation(summary = "List events for an organization")
    public List<EventResponse> listByOrganization(@PathVariable UUID orgId) {
        return service.listByOrganization(orgId);
    }

    @GetMapping("/events/{id}")
    @Operation(summary = "Get an event by ID")
    public EventResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PatchMapping("/events/{id}/status")
    @PreAuthorize("hasRole('ORGANIZER') and @authz.isEventOrganizationOwner(principal, #id)")
    @Operation(summary = "Update event status")
    public EventResponse updateStatus(@PathVariable UUID id,
                                      @Valid @RequestBody UpdateEventStatusRequest request) {
        return service.updateStatus(id, request);
    }
}
