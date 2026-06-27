package com.quori.event.dto;

import com.quori.event.entity.Event;
import com.quori.event.entity.EventStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EventResponse(
        UUID id,
        UUID organizationId,
        String name,
        String description,
        EventStatus status,
        LocalDate startDate,
        LocalDate endDate,
        Instant createdAt,
        Instant updatedAt
) {
    public static EventResponse from(Event entity) {
        return new EventResponse(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
