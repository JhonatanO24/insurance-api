package com.insurance_api.policies.infrastructure.persistence;

import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.enums.RatingStrategy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Entidad JPA — completamente separada del modelo de dominio.
 * coverage y riskProfile se persisten como JSONB en PostgreSQL.
 * El Mapper es el único que traduce entre esta entidad y Policy.
 */
@Entity
@Table(name = "policies")
@Getter
@Setter
@NoArgsConstructor
public class PolicyEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "policy_number", nullable = false, unique = true, length = 20)
    private String policyNumber;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_strategy", nullable = false, length = 15)
    private RatingStrategy ratingStrategy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private PolicyStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> coverage;

    @Column(name = "monthly_premium", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyPremium;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "risk_profile", columnDefinition = "jsonb")
    private Map<String, Object> riskProfile;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
