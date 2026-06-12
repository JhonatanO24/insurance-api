package com.insurance_api.policies.domain.states;

import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.exceptions.InvalidStateTransitionException;
import com.insurance_api.policies.domain.models.Policy;
import com.insurance_api.policies.domain.ports.PolicyStatePort;
import org.springframework.stereotype.Component;

/**
 * Estado ACTIVE — póliza vigente.
 * Transiciones válidas: SUSPENDED | CANCELLED
 */
@Component
public class ActiveState implements PolicyStatePort {

    @Override
    public PolicyStatus getStatus() {
        return PolicyStatus.ACTIVE;
    }

    @Override
    public void transitionTo(Policy policy, PolicyStatus targetStatus) {

        if (targetStatus == PolicyStatus.ACTIVE) {
            return;
        }

        if (targetStatus == PolicyStatus.SUSPENDED
                || targetStatus == PolicyStatus.CANCELLED) {
            policy.applyTransition(targetStatus);
            return;
        }

        throw new InvalidStateTransitionException(PolicyStatus.ACTIVE, targetStatus);
    }
}
