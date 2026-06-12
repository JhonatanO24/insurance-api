package com.insurance_api.policies.application.usecases;

import com.insurance_api.policies.application.dtos.PolicyResponseDto;
import com.insurance_api.policies.domain.exceptions.PolicyNotFoundException;
import com.insurance_api.policies.domain.ports.PolicyRepositoryPort;
import com.insurance_api.policies.application.dtos.PolicyDtoMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FindPolicyUseCase {

    private final PolicyRepositoryPort policyRepository;
    private final PolicyDtoMapper policyDtoMapper;

    public FindPolicyUseCase(PolicyRepositoryPort policyRepository, PolicyDtoMapper policyDtoMapper) {
        this.policyRepository = policyRepository;
        this.policyDtoMapper = policyDtoMapper;
    }

    public PolicyResponseDto execute(UUID id) {
        return policyRepository.findById(id)
                .map(policyDtoMapper::toResponse)
                .orElseThrow(() -> new PolicyNotFoundException(id));
    }
}
