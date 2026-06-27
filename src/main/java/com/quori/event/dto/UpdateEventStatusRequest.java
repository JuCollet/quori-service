package com.quori.event.dto;

import com.quori.event.entity.EventStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateEventStatusRequest(
        @NotNull EventStatus status
) {}
