package com.quori.user.dto;

import com.quori.user.entity.UserRole;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        String displayName,
        @NotNull UserRole role
) {}
