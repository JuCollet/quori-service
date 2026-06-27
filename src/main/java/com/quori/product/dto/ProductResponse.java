package com.quori.product.dto;

import com.quori.product.entity.Product;

import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID eventId,
        String name,
        String description,
        int priceCents,
        String category,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProductResponse from(Product entity) {
        return new ProductResponse(
                entity.getId(),
                entity.getEvent().getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPriceCents(),
                entity.getCategory(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
