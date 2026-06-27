package com.quori.event.service;

import com.quori.common.NotFoundException;
import com.quori.event.dto.CreateEventRequest;
import com.quori.event.dto.EventResponse;
import com.quori.event.dto.UpdateEventStatusRequest;
import com.quori.event.entity.Event;
import com.quori.event.entity.EventStatus;
import com.quori.event.repository.EventRepository;
import com.quori.organization.service.OrganizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class EventService {

    private static final Map<EventStatus, Set<EventStatus>> ALLOWED_TRANSITIONS = Map.of(
            EventStatus.DRAFT, Set.of(EventStatus.OPEN),
            EventStatus.OPEN, Set.of(EventStatus.CLOSED),
            EventStatus.CLOSED, Set.of(EventStatus.OPEN, EventStatus.SETTLED),
            EventStatus.SETTLED, Set.of()
    );

    private final EventRepository repository;
    private final OrganizationService organizationService;

    public EventService(EventRepository repository, OrganizationService organizationService) {
        this.repository = repository;
        this.organizationService = organizationService;
    }

    @Transactional
    public EventResponse create(UUID organizationId, CreateEventRequest request) {
        var organization = organizationService.findOrThrow(organizationId);
        var entity = new Event();
        entity.setOrganization(organization);
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setStartDate(request.startDate());
        entity.setEndDate(request.endDate());
        return EventResponse.from(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<EventResponse> listByOrganization(UUID organizationId) {
        return repository.findByOrganizationId(organizationId).stream()
                .map(EventResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventResponse getById(UUID id) {
        return EventResponse.from(findOrThrow(id));
    }

    @Transactional
    public EventResponse updateStatus(UUID id, UpdateEventStatusRequest request) {
        var entity = findOrThrow(id);
        var allowed = ALLOWED_TRANSITIONS.getOrDefault(entity.getStatus(), Set.of());
        if (!allowed.contains(request.status())) {
            throw new IllegalStateException(
                    "Cannot transition from " + entity.getStatus() + " to " + request.status());
        }
        entity.setStatus(request.status());
        return EventResponse.from(repository.save(entity));
    }

    public Event findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event", id));
    }
}
