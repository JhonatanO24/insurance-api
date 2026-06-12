package com.insurance_api.policies.application.strategies;

import com.insurance_api.policies.domain.exceptions.InvalidRiskProfileException;
import com.insurance_api.policies.domain.models.RiskProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Estrategias de tarificación — Tests unitarios")
class RatingStrategiesTest {

    private final BigDecimal AUTO_BASE   = new BigDecimal("120000");
    private final BigDecimal LIFE_BASE   = new BigDecimal("90000");
    private final BigDecimal HOME_BASE   = new BigDecimal("75000");
    private final BigDecimal HEALTH_BASE = new BigDecimal("180000");

    // ── STANDARD

    @Test
    @DisplayName("✅ STANDARD — prima sin ajuste para todos los ramos")
    void standardShouldReturnBasePremium() {
        StandardRatingStrategy strategy = new StandardRatingStrategy();
        RiskProfile profile = RiskProfile.empty();

        assertThat(strategy.calculatePremium(AUTO_BASE,   profile))
                .isEqualByComparingTo("120000");
        assertThat(strategy.calculatePremium(LIFE_BASE,   profile))
                .isEqualByComparingTo("90000");
        assertThat(strategy.calculatePremium(HOME_BASE,   profile))
                .isEqualByComparingTo("75000");
        assertThat(strategy.calculatePremium(HEALTH_BASE, profile))
                .isEqualByComparingTo("180000");
    }

    // ── RISK_BASED

    @Test
    @DisplayName("✅ RISK_BASED — LIFE + riskScore=50 → 135.000")
    void riskBasedLifeScore50ShouldReturn135000() {
        RiskBasedRatingStrategy strategy = new RiskBasedRatingStrategy();
        RiskProfile profile = RiskProfile.of(50, null);

        strategy.validate(profile);
        BigDecimal result = strategy.calculatePremium(LIFE_BASE, profile);

        assertThat(result).isEqualByComparingTo("135000.00");
    }

    @Test
    @DisplayName("✅ RISK_BASED — AUTO + riskScore=0 → 120.000 (sin ajuste)")
    void riskBasedScore0ShouldReturnBasePremium() {
        RiskBasedRatingStrategy strategy = new RiskBasedRatingStrategy();
        RiskProfile profile = RiskProfile.of(0, null);

        BigDecimal result = strategy.calculatePremium(AUTO_BASE, profile);

        assertThat(result).isEqualByComparingTo("120000.00");
    }

    @Test
    @DisplayName("❌ RISK_BASED sin riskScore → InvalidRiskProfileException")
    void riskBasedWithoutScoreShouldThrow() {
        RiskBasedRatingStrategy strategy = new RiskBasedRatingStrategy();
        RiskProfile profile = RiskProfile.empty();

        assertThatThrownBy(() -> strategy.validate(profile))
                .isInstanceOf(InvalidRiskProfileException.class)
                .hasMessageContaining("riskScore");
    }

    @Test
    @DisplayName("❌ RISK_BASED con riskScore=150 → fuera de rango")
    void riskBasedOutOfRangeShouldThrow() {
        RiskBasedRatingStrategy strategy = new RiskBasedRatingStrategy();
        RiskProfile profile = RiskProfile.of(150, null);

        assertThatThrownBy(() -> strategy.validate(profile))
                .isInstanceOf(InvalidRiskProfileException.class)
                .hasMessageContaining("100");
    }

    // ── LOYALTY

    @Test
    @DisplayName("✅ LOYALTY — HOME + customerSince=2020 → 63.750")
    void loyaltyHomeShouldReturn63750() {
        LoyaltyRatingStrategy strategy = new LoyaltyRatingStrategy();
        RiskProfile profile = RiskProfile.of(null, 2020);

        strategy.validate(profile);
        BigDecimal result = strategy.calculatePremium(HOME_BASE, profile);

        assertThat(result).isEqualByComparingTo("63750.00");
    }

    @Test
    @DisplayName("❌ LOYALTY sin customerSince → InvalidRiskProfileException")
    void loyaltyWithoutCustomerSinceShouldThrow() {
        LoyaltyRatingStrategy strategy = new LoyaltyRatingStrategy();
        RiskProfile profile = RiskProfile.empty();

        assertThatThrownBy(() -> strategy.validate(profile))
                .isInstanceOf(InvalidRiskProfileException.class)
                .hasMessageContaining("customerSince");
    }

    @Test
    @DisplayName("❌ LOYALTY con antigüedad < 2 años → InvalidRiskProfileException")
    void loyaltyInsufficientSeniorityShouldThrow() {
        LoyaltyRatingStrategy strategy = new LoyaltyRatingStrategy();
        RiskProfile profile = RiskProfile.of(null, 2025);

        assertThatThrownBy(() -> strategy.validate(profile))
                .isInstanceOf(InvalidRiskProfileException.class)
                .hasMessageContaining("antigüedad mínima");
    }
}
