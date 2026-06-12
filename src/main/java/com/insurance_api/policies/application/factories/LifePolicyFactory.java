package com.insurance_api.policies.application.factories;

import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.models.Coverage;
import com.insurance_api.policies.domain.ports.PolicyFactoryPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Factory Method - Ramo LIFE
 * coverageAmount     : 200.000.000
 * beneficiaryRequired: true
 * termMonths         : 12
 * Prima base         : 90.000
 */
@Component
public class LifePolicyFactory implements PolicyFactoryPort {

    private static final BigDecimal COVERAGE_AMOUNT      = new BigDecimal("200000000");
    private static final Boolean    BENEFICIARY_REQUIRED = Boolean.TRUE;
    private static final Integer    TERM_MONTHS          = 12;
    private static final BigDecimal BASE_PREMIUM         = new BigDecimal("90000");

    @Override
    public Branch getBranch() {
        return Branch.LIFE;
    }

    @Override
    public Coverage createDefaultCoverage() {
        return Coverage.builder()
                .coverageAmount(COVERAGE_AMOUNT)
                .beneficiaryRequired(BENEFICIARY_REQUIRED)
                .termMonths(TERM_MONTHS)
                .build();
    }

    @Override
    public BigDecimal getBasePremium() {
        return BASE_PREMIUM;
    }
}
