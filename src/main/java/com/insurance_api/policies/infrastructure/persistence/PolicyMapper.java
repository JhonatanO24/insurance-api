package com.insurance_api.policies.infrastructure.persistence;

import com.insurance_api.policies.domain.models.Coverage;
import com.insurance_api.policies.domain.models.Policy;
import com.insurance_api.policies.domain.models.RiskProfile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper bidireccional entre PolicyEntity (ORM) y Policy (dominio).

 * — toDomain : reconstruye el agregado desde la BD
 * — toEntity : convierte el agregado a entidad persistible

 * Coverage y RiskProfile se serializan/deserializan como Map<String, Object>
 * para persistirse como JSONB en PostgreSQL y reconstruirse
 * como Value Objects en el dominio.
 */
@Component
public class PolicyMapper {

    // ── ORM → Dominio ──
    public Policy toDomain(PolicyEntity entity) {
        return new Policy(
                entity.getId(),
                entity.getPolicyNumber(),
                entity.getCustomerId(),
                entity.getBranch(),
                entity.getRatingStrategy(),
                entity.getStatus(),
                coverageToDomain(entity.getCoverage()),
                entity.getMonthlyPremium(),
                riskProfileToDomain(entity.getRiskProfile()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // ── Dominio → ORM ──
    public PolicyEntity toEntity(Policy policy) {
        PolicyEntity entity = new PolicyEntity();
        entity.setId(policy.getId());
        entity.setPolicyNumber(policy.getPolicyNumber());
        entity.setCustomerId(policy.getCustomerId());
        entity.setBranch(policy.getBranch());
        entity.setRatingStrategy(policy.getRatingStrategy());
        entity.setStatus(policy.getStatus());
        entity.setCoverage(coverageToMap(policy.getCoverage()));
        entity.setMonthlyPremium(policy.getMonthlyPremium());
        entity.setRiskProfile(riskProfileToMap(policy.getRiskProfile()));
        entity.setCreatedAt(policy.getCreatedAt());
        entity.setUpdatedAt(policy.getUpdatedAt());
        return entity;
    }

    // ── Coverage: Map → Value Object ──
    private Coverage coverageToDomain(Map<String, Object> map) {
        if (map == null) return Coverage.builder().build();

        return Coverage.builder()
                .coverageAmount(toBigDecimal(map.get("coverageAmount")))
                .deductible(toBigDecimal(map.get("deductible")))
                .termMonths(toInteger(map.get("termMonths")))
                .beneficiaryRequired(toBoolean(map.get("beneficiaryRequired")))
                .copayRate(toDouble(map.get("copayRate")))
                .waitingPeriodDays(toInteger(map.get("waitingPeriodDays")))
                .build();
    }

    // ── Coverage: Value Object → Map ──
    private Map<String, Object> coverageToMap(Coverage coverage) {
        Map<String, Object> map = new HashMap<>();
        if (coverage.getCoverageAmount()      != null) map.put("coverageAmount",      coverage.getCoverageAmount());
        if (coverage.getDeductible()          != null) map.put("deductible",          coverage.getDeductible());
        if (coverage.getTermMonths()          != null) map.put("termMonths",          coverage.getTermMonths());
        if (coverage.getBeneficiaryRequired() != null) map.put("beneficiaryRequired", coverage.getBeneficiaryRequired());
        if (coverage.getCopayRate()           != null) map.put("copayRate",           coverage.getCopayRate());
        if (coverage.getWaitingPeriodDays()   != null) map.put("waitingPeriodDays",   coverage.getWaitingPeriodDays());
        return map;
    }

    // ── RiskProfile: Map → Value Object ──
    private RiskProfile riskProfileToDomain(Map<String, Object> map) {
        if (map == null) return RiskProfile.empty();
        return RiskProfile.of(
                toInteger(map.get("riskScore")),
                toInteger(map.get("customerSince"))
        );
    }

    // ── RiskProfile: Value Object → Map ──
    private Map<String, Object> riskProfileToMap(RiskProfile riskProfile) {
        Map<String, Object> map = new HashMap<>();
        if (riskProfile.getRiskScore()     != null) map.put("riskScore",     riskProfile.getRiskScore());
        if (riskProfile.getCustomerSince() != null) map.put("customerSince", riskProfile.getCustomerSince());
        return map;
    }

    // ── Helpers de conversión de tipos desde JSON ──
    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n)     return new BigDecimal(n.toString());
        return new BigDecimal(value.toString());
    }

    private Integer toInteger(Object value) {
        if (value == null)          return null;
        if (value instanceof Integer i) return i;
        if (value instanceof Number n)  return n.intValue();
        return Integer.parseInt(value.toString());
    }

    private Boolean toBoolean(Object value) {
        if (value == null)             return null;
        if (value instanceof Boolean b) return b;
        return Boolean.parseBoolean(value.toString());
    }

    private Double toDouble(Object value) {
        if (value == null)           return null;
        if (value instanceof Double d) return d;
        if (value instanceof Number n) return n.doubleValue();
        return Double.parseDouble(value.toString());
    }
}
