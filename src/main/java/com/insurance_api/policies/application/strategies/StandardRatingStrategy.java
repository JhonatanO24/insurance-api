package com.insurance_api.policies.application.strategies;

import com.insurance_api.policies.domain.enums.RatingStrategy;
import com.insurance_api.policies.domain.models.RiskProfile;
import com.insurance_api.policies.domain.ports.RatingStrategyPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy - STANDARD
 * Prima resultante: base sin ajuste.
 * Sin validaciones adicionales de riskProfile.
 */
@Component
public class StandardRatingStrategy implements RatingStrategyPort {

    @Override
    public RatingStrategy getName() {
        return RatingStrategy.STANDARD;
    }

    @Override
    public void validate(RiskProfile riskProfile) {
        // STANDARD no requiere campos específicos en riskProfile
    }

    @Override
    public BigDecimal calculatePremium(BigDecimal basePremium, RiskProfile riskProfile) {
        return basePremium;
    }
}
