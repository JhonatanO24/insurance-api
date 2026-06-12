package com.insurance_api.policies.application.builders;

import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.enums.RatingStrategy;
import com.insurance_api.policies.domain.models.Coverage;
import com.insurance_api.policies.domain.models.Policy;
import com.insurance_api.policies.domain.models.RiskProfile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Builder fluido para el agregado Policy.

 * - Cada setter retorna this (fluent API).
 * - build() valida campos obligatorios antes de construir.
 * - Asigna QUOTED como estado inicial siempre - sin excepción.
 * - Scope PROTOTYPE: cada use case recibe una instancia nueva
 *   y limpia, sin estado residual de construcciones anteriores
 */
@Component
@Scope("prototype")
public class PolicyBuilder {

    private UUID id;
    private String         policyNumber;
    private UUID           customerId;
    private Branch         branch;
    private RatingStrategy ratingStrategy;
    private Coverage       coverage;
    private BigDecimal     monthlyPremium;
    private RiskProfile    riskProfile;

    // ── Setters fluidos ──

    public PolicyBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public PolicyBuilder policyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
        return this;
    }

    public PolicyBuilder customerId(UUID customerId) {
        this.customerId = customerId;
        return this;
    }

    public PolicyBuilder branch(Branch branch) {
        this.branch = branch;
        return this;
    }

    public PolicyBuilder ratingStrategy(RatingStrategy ratingStrategy) {
        this.ratingStrategy = ratingStrategy;
        return this;
    }

    public PolicyBuilder coverage(Coverage coverage) {
        this.coverage = coverage;
        return this;
    }

    public PolicyBuilder monthlyPremium(BigDecimal monthlyPremium) {
        this.monthlyPremium = monthlyPremium;
        return this;
    }

    public PolicyBuilder riskProfile(RiskProfile riskProfile) {
        this.riskProfile = riskProfile;
        return this;
    }

    /**
     * Valida todos los campos obligatorios y construye el agregado.
     * El estado inicial siempre es QUOTED — nunca configurable desde afuera.
     *
     * @throws IllegalStateException si algún campo obligatorio está ausente
     */
    public Policy build() {
        validate();

        LocalDateTime now = LocalDateTime.now();

        return new Policy(
                id,
                policyNumber,
                customerId,
                branch,
                ratingStrategy,
                PolicyStatus.QUOTED,   // ← estado inicial siempre QUOTED
                coverage,
                monthlyPremium,
                riskProfile != null ? riskProfile : RiskProfile.empty(),
                now,
                now
        );
    }

    // ── Validación interna ──

    private void validate() {
        List<String> missing = new ArrayList<>();

        if (id             == null) missing.add("id");
        if (policyNumber   == null || policyNumber.isBlank()) missing.add("policyNumber");
        if (customerId     == null) missing.add("customerId");
        if (branch         == null) missing.add("branch");
        if (ratingStrategy == null) missing.add("ratingStrategy");
        if (coverage       == null) missing.add("coverage");
        if (monthlyPremium == null) missing.add("monthlyPremium");

        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "No se puede construir la póliza. Campos obligatorios faltantes: "
                            + String.join(", ", missing)
            );
        }

        if (monthlyPremium.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(
                    "monthlyPremium debe ser mayor a cero"
            );
        }
    }
}
