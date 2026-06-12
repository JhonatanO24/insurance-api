package com.insurance_api.policies.domain.models;

import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.enums.RatingStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Agregado raíz del módulo policies.
 * — Estado encapsulado: sin setters públicos arbitrarios.
 * — Coverage y RiskProfile son inmutables tras la creación.
 * — La transición de estado se delega a PolicyStatePort (patrón State).
 */
@Getter
@AllArgsConstructor
public class Policy {

    private final UUID id;
    private final String policyNumber;
    private final UUID customerId;
    private final Branch branch;
    private final RatingStrategy ratingStrategy;
    private PolicyStatus status;
    private final Coverage coverage;
    private final BigDecimal monthlyPremium;
    private final RiskProfile riskProfile;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Único punto de mutación permitido en el agregado.
     * Solo el patrón State llama este método a través del use case.
     */
    public void applyTransition(PolicyStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
}
