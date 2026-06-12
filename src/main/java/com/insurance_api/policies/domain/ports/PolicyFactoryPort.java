package com.insurance_api.policies.domain.ports;

import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.models.Coverage;

import java.math.BigDecimal;

/**
 * Contrato que toda factory de ramo debe cumplir.
 * Factory Method: cada implementación conoce su ramo
 * y su cobertura por defecto - el use case no decide nada con switch.
 */
public interface PolicyFactoryPort {
    Branch     getBranch();
    Coverage   createDefaultCoverage();
    BigDecimal getBasePremium();
}
