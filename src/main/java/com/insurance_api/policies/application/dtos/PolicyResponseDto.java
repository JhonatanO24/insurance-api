package com.insurance_api.policies.application.dtos;

import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.enums.RatingStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PolicyResponseDto(
        UUID             id,
        String           policyNumber,
        UUID             customerId,
        Branch           branch,
        RatingStrategy   ratingStrategy,
        PolicyStatus     status,
        CoverageResponseDto coverage,
        BigDecimal       monthlyPremium,
        Integer          riskScore,
        Integer          customerSince,
        LocalDateTime    createdAt,
        LocalDateTime    updatedAt
) {}
