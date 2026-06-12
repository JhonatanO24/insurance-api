package com.insurance_api.policies.application.factories;

import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.models.Coverage;
import com.insurance_api.policies.domain.ports.PolicyFactoryPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Factory Method - Ramo HEALTH
 * coverageAmount    : 100.000.000
 * copayRate         : 0.20
 * waitingPeriodDays : 30
 * Prima base        : 180.000
 */
@Component
public class HealthPolicyFactory implements PolicyFactoryPort {

    private static final BigDecimal COVERAGE_AMOUNT     = new BigDecimal("100000000");
    private static final Double     COPAY_RATE          = 0.20;
    private static final Integer    WAITING_PERIOD_DAYS = 30;
    private static final BigDecimal BASE_PREMIUM        = new BigDecimal("180000");

    @Override
    public Branch getBranch() {
        return Branch.HEALTH;
    }

    @Override
    public Coverage createDefaultCoverage() {
        return Coverage.builder()
                .coverageAmount(COVERAGE_AMOUNT)
                .copayRate(COPAY_RATE)
                .waitingPeriodDays(WAITING_PERIOD_DAYS)
                .build();
    }

    @Override
    public BigDecimal getBasePremium() {
        return BASE_PREMIUM;
    }
}
