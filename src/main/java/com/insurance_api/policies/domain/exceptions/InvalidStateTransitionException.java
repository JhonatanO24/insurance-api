package com.insurance_api.policies.domain.exceptions;

import com.insurance_api.policies.domain.enums.PolicyStatus;

public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(PolicyStatus from, PolicyStatus to) {
        super("Transición de estado inválido: " + from + " → " + to);
    }
}
