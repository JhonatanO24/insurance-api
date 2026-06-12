package com.insurance_api.policies.infrastructure.persistence;

import com.insurance_api.policies.domain.models.Policy;
import com.insurance_api.policies.domain.ports.PolicyRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del port del dominio usando Spring Data JPA.
 * El dominio solo conoce PolicyRepositoryPort — nunca esta clase.
 */
@Repository
public class PolicyRepositoryAdapter implements PolicyRepositoryPort {

    private final PolicyJpaRepository jpaRepository;
    private final PolicyMapper        mapper;

    public PolicyRepositoryAdapter(PolicyJpaRepository jpaRepository,
                                   PolicyMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper        = mapper;
    }

    @Override
    public Policy save(Policy policy) {
        PolicyEntity entity = mapper.toEntity(policy);
        PolicyEntity saved  = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Policy> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Policy> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerId(customerId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByPolicyNumber(String policyNumber) {
        return jpaRepository.existsByPolicyNumber(policyNumber);
    }
}

