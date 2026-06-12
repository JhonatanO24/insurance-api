package com.insurance_api.policies.domain.states;

import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.exceptions.InvalidStateTransitionException;
import com.insurance_api.policies.domain.models.Policy;
import com.insurance_api.policies.domain.ports.PolicyStatePort;
import org.springframework.stereotype.Component;

/**
 * Estado ISSUED — póliza emitida, pendiente de activación.
 * Transiciones válidas: ACTIVE | CANCELLED
 */
@Component
public class IssuedState implements PolicyStatePort {

    @Override
    public PolicyStatus getStatus() {
        return PolicyStatus.ISSUED;
    }

    @Override
    public void transitionTo(Policy policy, PolicyStatus targetStatus) {

        if (targetStatus == PolicyStatus.ISSUED) {
            return;
        }

        if (targetStatus == PolicyStatus.ACTIVE
                || targetStatus == PolicyStatus.CANCELLED) {
            policy.applyTransition(targetStatus);
            return;
        }

        throw new InvalidStateTransitionException(PolicyStatus.ISSUED, targetStatus);
    }
}
