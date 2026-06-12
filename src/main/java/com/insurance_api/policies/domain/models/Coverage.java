package com.insurance_api.policies.domain.models;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * Value Object — inmutable.
 * Representa la cobertura de una póliza según su ramo.
 * AUTO  : coverageAmount, deductible, termMonths
 * LIFE  : coverageAmount, beneficiaryRequired, termMonths
 * HOME  : coverageAmount, deductible, termMonths
 * HEALTH: coverageAmount, copayRate, waitingPeriodDays
 */
@Value
@Builder
public class Coverage {

    BigDecimal coverageAmount;
    BigDecimal deductible;          // AUTO, HOME
    Integer termMonths;             // AUTO, LIFE, HOME
    Boolean beneficiaryRequired;    // LIFE
    Double copayRate;               // HEALTH
    Integer waitingPeriodDays;      // HEALTH

}