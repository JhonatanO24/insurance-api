package com.insurance_api.policies.application.strategies;

import com.insurance_api.policies.domain.enums.RatingStrategy;
import com.insurance_api.policies.domain.exceptions.InvalidRiskProfileException;
import com.insurance_api.policies.domain.models.RiskProfile;
import com.insurance_api.policies.domain.ports.RatingStrategyPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Strategy - RISK_BASED
 * Prima resultante: base x (1 + riskScore / 100)
 * Validación: riskScore obligatorio y en rango [0, 100]
 */
@Component
public class RiskBasedRatingStrategy implements RatingStrategyPort {

    private static final int    MIN_RISK_SCORE = 0;
    private static final int    MAX_RISK_SCORE = 100;
    private static final BigDecimal DIVISOR    = new BigDecimal("100");

    @Override
    public RatingStrategy getName() {
        return RatingStrategy.RISK_BASED;
    }

    @Override
    public void validate(RiskProfile riskProfile) {
        if (riskProfile.getRiskScore() == null) {
            throw new InvalidRiskProfileException(
                    "La estrategia RISK_BASED requiere riskScore en riskProfile"
            );
        }
        if (riskProfile.getRiskScore() < MIN_RISK_SCORE
                || riskProfile.getRiskScore() > MAX_RISK_SCORE) {
            throw new InvalidRiskProfileException(
                    "riskScore debe estar entre " + MIN_RISK_SCORE
                            + " y " + MAX_RISK_SCORE
                            + ", valor recibido: " + riskProfile.getRiskScore()
            );
        }
    }

    @Override
    public BigDecimal calculatePremium(BigDecimal basePremium, RiskProfile riskProfile) {
        // base × (1 + riskScore / 100)
        BigDecimal factor = BigDecimal.ONE
                .add(new BigDecimal(riskProfile.getRiskScore()).divide(DIVISOR));
        return basePremium.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
