package com.insurance_api.policies.domain.ports;

import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.models.Policy;

/**
 * Contrato que cada estado del ciclo de vida debe cumplir.
 * State: cada estado concreto declara sus transiciones válidas.
 * El use case delega aquí - no contiene la matriz de transiciones.
 */
public interface PolicyStatePort {
    PolicyStatus getStatus();
    void         transitionTo(Policy policy, PolicyStatus targetStatus);
}
