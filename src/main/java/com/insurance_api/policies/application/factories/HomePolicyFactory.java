package com.insurance_api.policies.application.factories;

import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.models.Coverage;
import com.insurance_api.policies.domain.ports.PolicyFactoryPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Factory Method - Ramo HOME
 * coverageAmount : 150.000.000
 * deductible     : 2.000.000
 * termMonths     : 12
 * Prima base     : 75.000
 */
@Component
public class HomePolicyFactory implements PolicyFactoryPort {

    private static final BigDecimal COVERAGE_AMOUNT = new BigDecimal("150000000");
    private static final BigDecimal DEDUCTIBLE      = new BigDecimal("2000000");
    private static final Integer    TERM_MONTHS     = 12;
    private static final BigDecimal BASE_PREMIUM    = new BigDecimal("75000");

    @Override
    public Branch getBranch() {
        return Branch.HOME;
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
