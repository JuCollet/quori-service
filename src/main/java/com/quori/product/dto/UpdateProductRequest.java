package com.quori.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record UpdateProductRequest(
        @NotBlank String name,
        String description,
        @Positive int priceCents,
        String category,
        boolean active
) {}
