package com.insurance_api.policies.application.factories;

import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.models.Coverage;
import com.insurance_api.policies.domain.ports.PolicyFactoryPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Factory Method - Ramo AUTO
 * coverageAmount : 80.000.000
 * deductible     : 1.000.000
 * termMonths     : 12
 * Prima base     : 120.000
 */
@Component
public class AutoPolicyFactory implements PolicyFactoryPort {

    private static final BigDecimal COVERAGE_AMOUNT = new BigDecimal("80000000");
    private static final BigDecimal DEDUCTIBLE      = new BigDecimal("1000000");
    private static final Integer    TERM_MONTHS     = 12;
    private static final BigDecimal BASE_PREMIUM    = new BigDecimal("120000");

    @Override
    public Branch getBranch() {
        return Branch.AUTO;
    }

    @Override
    public Coverage createDefaultCoverage() {
        return Coverage.builder()
                .coverageAmount(COVERAGE_AMOUNT)
                .deductible(DEDUCTIBLE)
                .termMonths(TERM_MONTHS)
                .build();
    }

    @Override
    public BigDecimal getBasePremium() {
        return BASE_PREMIUM;
    }
}
