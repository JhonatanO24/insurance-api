package com.insurance_api.policies.domain.states;

import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.ports.PolicyStatePort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Registro de estados del ciclo de vida.
 * El use case lo usa para resolver el estado actual
 * sin ningún switch ni instanceof.

 * OCP: agregar un estado nuevo = clase nueva + @Component.
 */
@Component
public class PolicyStateRegistry {

    private final Map<PolicyStatus, PolicyStatePort> states;

    public PolicyStateRegistry(List<PolicyStatePort> stateList) {
        this.states = stateList.stream()
                .collect(Collectors.toMap(
                        PolicyStatePort::getStatus,
                        Function.identity()
                ));
    }

    public PolicyStatePort getState(PolicyStatus status) {
        return states.get(status);
    }
}

