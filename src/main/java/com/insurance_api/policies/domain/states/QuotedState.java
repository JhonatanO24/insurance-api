package com.insurance_api.policies.domain.states;

import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.exceptions.InvalidStateTransitionException;
import com.insurance_api.policies.domain.models.Policy;
import com.insurance_api.policies.domain.ports.PolicyStatePort;
import org.springframework.stereotype.Component;

/**
 * Estado QUOTED — póliza recién cotizada.
 * Transiciones válidas: ISSUED | CANCELLED
 */
@Component
public class QuotedState implements PolicyStatePort {

    @Override
    public PolicyStatus getStatus() {
        return PolicyStatus.QUOTED;
    }

    @Override
    public void transitionTo(Policy policy, PolicyStatus targetStatus) {

        // Idempotente — transicionar al estado actual no es error
        if (targetStatus == PolicyStatus.QUOTED) {
            return;
        }

        if (targetStatus == PolicyStatus.ISSUED
                || targetStatus == PolicyStatus.CANCELLED) {
            policy.applyTransition(targetStatus);
            return;
        }

        throw new InvalidStateTransitionException(PolicyStatus.QUOTED, targetStatus);
    }
}
