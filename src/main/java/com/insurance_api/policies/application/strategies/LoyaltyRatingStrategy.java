package com.insurance_api.policies.application.strategies;

import com.insurance_api.policies.domain.enums.RatingStrategy;
import com.insurance_api.policies.domain.exceptions.InvalidRiskProfileException;
import com.insurance_api.policies.domain.models.RiskProfile;
import com.insurance_api.policies.domain.ports.RatingStrategyPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;

/**
 * Strategy - LOYALTY
 * Prima resultante: base x 0.85 (15% de descuento)
 * Validación:
 *   - customerSince obligatorio
 *   - antiguedad mínima de 2 años
 */
@Component
public class LoyaltyRatingStrategy implements RatingStrategyPort {

    private static final BigDecimal LOYALTY_FACTOR      = new BigDecimal("0.85");
    private static final int        MIN_SENIORITY_YEARS = 2;

    @Override
    public RatingStrategy getName() {
        return RatingStrategy.LOYALTY;
    }

    @Override
    public void validate(RiskProfile riskProfile) {
        if (riskProfile.getCustomerSince() == null) {
            throw new InvalidRiskProfileException(
                    "La estrategia LOYALTY requiere customerSince en riskProfile"
            );
        }

        int currentYear = Year.now().getValue();
        int seniority   = currentYear - riskProfile.getCustomerSince();

        if (seniority < MIN_SENIORITY_YEARS) {
            throw new InvalidRiskProfileException(
                    "La estrategia LOYALTY requiere antigüedad mínima de "
                            + MIN_SENIORITY_YEARS + " años. "
                            + "Antigüedad actual: " + seniority + " año(s)"
            );
        }
    }

    @Override
    public BigDecimal calculatePremium(BigDecimal basePremium, RiskProfile riskProfile) {
        // base × 0.85
        return basePremium.multiply(LOYALTY_FACTOR).setScale(2, RoundingMode.HALF_UP);
    }
}
