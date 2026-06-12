package com.insurance_api.policies.domain.states;

import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.exceptions.InvalidStateTransitionException;
import com.insurance_api.policies.domain.models.Policy;
import com.insurance_api.policies.domain.ports.PolicyStatePort;
import org.springframework.stereotype.Component;

/**
 * Estado CANCELLED — estado terminal.
 * No admite ninguna transición — lanza excepción siempre.
 * Idempotente solo si el target es también CANCELLED.
 */
@Component
public class CancelledState implements PolicyStatePort {

    @Override
    public PolicyStatus getStatus() {
        return PolicyStatus.CANCELLED;
    }

    @Override
    public void transitionTo(Policy policy, PolicyStatus targetStatus) {

        if (targetStatus == PolicyStatus.CANCELLED) {
            return;
        }

        // Terminal — ninguna transición permitida
        throw new InvalidStateTransitionException(PolicyStatus.CANCELLED, targetStatus);
    }
}
