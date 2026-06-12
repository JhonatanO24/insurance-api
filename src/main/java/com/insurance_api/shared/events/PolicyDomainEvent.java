package com.insurance_api.shared.events;

import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.enums.PolicyStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payload del evento publicado al broker en cada transición.
 * Inmutable - se construye una vez y se publica.
 */
public record PolicyDomainEvent(
        String        eventType,      // policy.issued, policy.activated, etc.
        UUID          policyId,
        String        policyNumber,
        UUID          customerId,
        Branch        branch,
        PolicyStatus  oldStatus,
        PolicyStatus  newStatus,
        LocalDateTime timestamp
) {
    public static PolicyDomainEvent of(String eventType, UUID policyId,
                                       String policyNumber, UUID customerId,
                                       Branch branch, PolicyStatus oldStatus,
                                       PolicyStatus newStatus) {
        return new PolicyDomainEvent(
                eventType, policyId, policyNumber,
                customerId, branch, oldStatus, newStatus,
                LocalDateTime.now()
        );
    }
}