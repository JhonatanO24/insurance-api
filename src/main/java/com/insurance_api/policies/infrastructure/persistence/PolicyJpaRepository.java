package com.insurance_api.policies.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PolicyJpaRepository extends JpaRepository<PolicyEntity, UUID> {
    List<PolicyEntity> findByCustomerId(UUID customerId);
    boolean existsByPolicyNumber(String policyNumber);
}
