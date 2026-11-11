package com.quori.organization.dto;

import com.quori.organization.entity.Organization;

import java.time.Instant;
import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        UUID ownerId,
        String ownerDisplayName,
        Instant createdAt,
        Instant updatedAt
) {
    public static OrganizationResponse from(Organization entity) {
        return new OrganizationResponse(
                entity.getId(),
                entity.getName(),
                entity.getOwner().getId(),
                entity.getOwner().getDisplayName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
