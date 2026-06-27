package com.quori.event.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateEventRequest(
        @NotBlank String name,
        String description,
        LocalDate startDate,
        LocalDate endDate
) {}
