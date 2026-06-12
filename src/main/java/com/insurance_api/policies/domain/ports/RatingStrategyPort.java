package com.insurance_api.policies.domain.ports;

import com.insurance_api.policies.domain.enums.RatingStrategy;
import com.insurance_api.policies.domain.models.RiskProfile;

import java.math.BigDecimal;

/**
 * Contrato que toda estrategia de tarificación debe cumplir.
 * Strategy: algoritmos intercambiables sin switch en el use case.
 */
public interface RatingStrategyPort {
    RatingStrategy getName();
    void           validate(RiskProfile riskProfile);
    BigDecimal     calculatePremium(BigDecimal basePremium, RiskProfile riskProfile);
}
