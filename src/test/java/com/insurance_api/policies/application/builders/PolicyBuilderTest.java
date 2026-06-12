package com.insurance_api.policies.application.builders;

import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.enums.RatingStrategy;
import com.insurance_api.policies.domain.models.Coverage;
import com.insurance_api.policies.domain.models.Policy;
import com.insurance_api.policies.domain.models.RiskProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PolicyBuilder — Tests unitarios")
class PolicyBuilderTest {

    @Test
    @DisplayName("✅ build() exitoso → Policy con estado QUOTED")
    void shouldBuildPolicyWithQuotedStatus() {
        Coverage coverage = Coverage.builder()
                .coverageAmount(new BigDecimal("80000000"))
                .termMonths(12)
                .build();

        Policy policy = new PolicyBuilder()
                .id(UUID.randomUUID())
                .policyNumber("POL-2026-000001")
                .customerId(UUID.randomUUID())
                .branch(Branch.AUTO)
                .ratingStrategy(RatingStrategy.STANDARD)
                .coverage(coverage)
                .monthlyPremium(new BigDecimal("120000"))
                .riskProfile(RiskProfile.empty())
                .build();

        assertThat(policy).isNotNull();
        assertThat(policy.getStatus()).isEqualTo(PolicyStatus.QUOTED);
        assertThat(policy.getBranch()).isEqualTo(Branch.AUTO);
        assertThat(policy.getMonthlyPremium())
                .isEqualByComparingTo("120000");
    }

    @Test
    @DisplayName("❌ build() sin campos obligatorios → IllegalStateException")
    void shouldThrowWhenMandatoryFieldsMissing() {
        assertThatThrownBy(() -> new PolicyBuilder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Campos obligatorios faltantes");
    }

    @Test
    @DisplayName("❌ build() con monthlyPremium <= 0 → IllegalStateException")
    void shouldThrowWhenPremiumIsZeroOrNegative() {
        Coverage coverage = Coverage.builder()
                .coverageAmount(new BigDecimal("80000000")).build();

        assertThatThrownBy(() -> new PolicyBuilder()
                .id(UUID.randomUUID())
                .policyNumber("POL-2026-000001")
                .customerId(UUID.randomUUID())
                .branch(Branch.AUTO)
                .ratingStrategy(RatingStrategy.STANDARD)
                .coverage(coverage)
                .monthlyPremium(BigDecimal.ZERO)
                .build()
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("mayor a cero");
    }
}
