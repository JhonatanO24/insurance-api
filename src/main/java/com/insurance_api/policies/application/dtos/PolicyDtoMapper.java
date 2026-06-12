package com.insurance_api.policies.application.dtos;

import com.insurance_api.policies.domain.models.Coverage;
import com.insurance_api.policies.domain.models.Policy;
import org.springframework.stereotype.Component;

@Component
public class PolicyDtoMapper {

    public PolicyResponseDto toResponse(Policy policy) {
        return new PolicyResponseDto(
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getCustomerId(),
                policy.getBranch(),
                policy.getRatingStrategy(),
                policy.getStatus(),
                toCoverageResponse(policy.getCoverage()),
                policy.getMonthlyPremium(),
                policy.getRiskProfile().getRiskScore(),
                policy.getRiskProfile().getCustomerSince(),
                policy.getCreatedAt(),
                policy.getUpdatedAt()
        );
    }

    private CoverageResponseDto toCoverageResponse(Coverage coverage) {
        return new CoverageResponseDto(
                coverage.getCoverageAmount(),
                coverage.getDeductible(),
                coverage.getTermMonths(),
                coverage.getBeneficiaryRequired(),
                coverage.getCopayRate(),
                coverage.getWaitingPeriodDays()
        );
    }
}