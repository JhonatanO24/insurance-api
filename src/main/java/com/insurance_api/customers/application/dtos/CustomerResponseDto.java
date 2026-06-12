package com.insurance_api.customers.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponseDto (
        UUID id,
        String name,
        String email,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
