package com.insurance_api.policies.application.factories;

import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.exceptions.UnsupportedBranchException;
import com.insurance_api.policies.domain.ports.PolicyFactoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Registro de Factories por ramo.

 * OCP en acción: agregar un 5º ramo = crear una nueva clase
 * que implemente PolicyFactoryPort + anotarla con @Component.
 * Este registro la detecta automáticamente — CERO cambios aquí.

 * DIP: el use case inyecta este registro y nunca hace new de factories.
 */
@Component
public class PolicyFactoryRegistry {

    private final Map<Branch, PolicyFactoryPort> factories;

    // Spring inyecta automáticamente TODAS las implementaciones de PolicyFactoryPort
    public PolicyFactoryRegistry(List<PolicyFactoryPort> factoryList) {
        this.factories = factoryList.stream()
                .collect(Collectors.toMap(
                        PolicyFactoryPort::getBranch,
                        Function.identity()
                ));
    }

    public PolicyFactoryPort getFactory(Branch branch) {
        PolicyFactoryPort factory = factories.get(branch);
        if (factory == null) {
            throw new UnsupportedBranchException(branch.name());
        }
        return factory;
    }
}
