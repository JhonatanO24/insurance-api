package com.insurance_api.policies.domain.states;

import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.exceptions.InvalidStateTransitionException;
import com.insurance_api.policies.domain.models.Policy;
import com.insurance_api.policies.domain.ports.PolicyStatePort;
import org.springframework.stereotype.Component;

/**
 * Estado SUSPENDED — póliza suspendida temporalmente.
 * Transiciones válidas: ACTIVE | CANCELLED
 */
@Component
public class SuspendedState implements PolicyStatePort {

    @Override
    public PolicyStatus getStatus() {
        return PolicyStatus.SUSPENDED;
    }

    @Override
    public void transitionTo(Policy policy, PolicyStatus targetStatus) {

        if (targetStatus == PolicyStatus.SUSPENDED) {
            return;
        }

        if (targetStatus == PolicyStatus.ACTIVE
                || targetStatus == PolicyStatus.CANCELLED) {
            policy.applyTransition(targetStatus);
            return;
        }

        throw new InvalidStateTransitionException(PolicyStatus.SUSPENDED, targetStatus);
    }
}
