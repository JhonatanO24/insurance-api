package com.insurance_api.policies.domain.ports;

import com.insurance_api.policies.domain.models.Policy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PolicyRepositoryPort {
    Policy save(Policy policy);
    Optional<Policy> findById(UUID id);
    List<Policy> findByCustomerId(UUID customerId);
    boolean existsByPolicyNumber(String policyNumber);
}
