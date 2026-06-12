package com.insurance_api.policies.application.dtos;

import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.enums.RatingStrategy;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO de entrada para cotizar/crear una póliza.
 * La cobertura NO viene del request - la define la factory del ramo.
 * La prima NO viene del request - la calcula la strategy.
 */
public record CreatePolicyDto(

        @NotNull(message = "El customerId es obligatorio")
        UUID customerId,

        @NotNull(message = "El ramo (branch) es obligatorio")
        Branch branch,

        @NotNull(message = "La estrategia de tarificación es obligatoria")
        RatingStrategy ratingStrategy,

        // Opcional — requerido solo por RISK_BASED y LOYALTY
        Integer riskScore,

        // Opcional — requerido solo por LOYALTY
        Integer customerSince
) {}
