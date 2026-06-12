package com.insurance_api.policies.application.dtos;

import java.math.BigDecimal;

public record CoverageResponseDto(
        BigDecimal coverageAmount,
        BigDecimal deductible,
        Integer    termMonths,
        Boolean    beneficiaryRequired,
        Double     copayRate,
        Integer    waitingPeriodDays
) {}
