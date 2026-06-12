package com.insurance_api.policies.application.usecases;

import com.insurance_api.policies.application.dtos.PolicyResponseDto;
import com.insurance_api.policies.domain.ports.PolicyRepositoryPort;
import com.insurance_api.policies.application.dtos.PolicyDtoMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FindCustomerPoliciesUseCase {

    private final PolicyRepositoryPort policyRepository;
    private final PolicyDtoMapper policyDtoMapper;

    public FindCustomerPoliciesUseCase(PolicyRepositoryPort policyRepository, PolicyDtoMapper policyDtoMapper) {
        this.policyRepository = policyRepository;
        this.policyDtoMapper = policyDtoMapper;
    }

    public List<PolicyResponseDto> execute(UUID customerId) {
        return policyRepository.findByCustomerId(customerId).stream()
                .map(policyDtoMapper::toResponse)
                .toList();
    }
}
