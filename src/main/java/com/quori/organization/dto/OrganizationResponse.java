package com.quori.organization.dto;

import com.quori.organization.entity.Organization;

import java.time.Instant;
import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        String ownerEmail,
        Instant createdAt,
        Instant updatedAt
) {
    public static OrganizationResponse from(Organization entity) {
        return new OrganizationResponse(
                entity.getId(),
                entity.getName(),
                entity.getOwnerEmail(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
