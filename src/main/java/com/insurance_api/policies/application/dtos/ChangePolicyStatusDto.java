package com.insurance_api.policies.application.dtos;

import com.insurance_api.policies.domain.enums.PolicyStatus;
import jakarta.validation.constraints.NotNull;

public record ChangePolicyStatusDto(

        @NotNull(message = "El targetStatus es obligatorio")
        PolicyStatus targetStatus
) {}
