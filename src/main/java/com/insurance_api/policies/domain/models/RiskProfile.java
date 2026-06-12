package com.insurance_api.policies.domain.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Value Object — inmutable.
 * Contiene los datos de riesgo del cliente para la tarificación.
 * riskScore     : requerido por RISK_BASED  (0-100)
 * customerSince : requerido por LOYALTY     (año, ej: 2020)
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RiskProfile {

    Integer riskScore;
    Integer customerSince;

    public static RiskProfile of(Integer riskScore, Integer customerSince) {
        return new RiskProfile(riskScore, customerSince);
    }

    public static RiskProfile empty() {
        return new RiskProfile(null, null);
    }
}
