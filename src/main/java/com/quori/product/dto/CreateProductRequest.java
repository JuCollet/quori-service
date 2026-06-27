package com.quori.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateProductRequest(
        @NotBlank String name,
        String description,
        @Positive int priceCents,
        String category
) {}
