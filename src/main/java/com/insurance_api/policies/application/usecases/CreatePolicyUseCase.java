package com.insurance_api.policies.application.usecases;

import com.insurance_api.customers.domain.exceptions.CustomerNotFoundException;
import com.insurance_api.customers.domain.ports.CustomerRepositoryPort;
import com.insurance_api.policies.application.builders.PolicyBuilder;
import com.insurance_api.policies.application.dtos.CreatePolicyDto;
import com.insurance_api.policies.application.dtos.PolicyResponseDto;
import com.insurance_api.policies.application.factories.PolicyFactoryRegistry;
import com.insurance_api.policies.application.strategies.RatingStrategyRegistry;
import com.insurance_api.policies.domain.models.Coverage;
import com.insurance_api.policies.domain.models.Policy;
import com.insurance_api.policies.domain.models.RiskProfile;
import com.insurance_api.policies.domain.ports.PolicyFactoryPort;
import com.insurance_api.policies.domain.ports.PolicyRepositoryPort;
import com.insurance_api.policies.domain.ports.RatingStrategyPort;
import com.insurance_api.policies.application.dtos.PolicyDtoMapper;
import com.insurance_api.shared.siingleton.PolicyNumberSequencer;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Orquesta la creación de una póliza integrando:
 * - Factory Method  : cobertura por defecto según ramo
 * - Strategy        : cálculo de prima según estrategia
 * - Builder         : ensamblado validado del agregado
 *
 * NO contiene switch, instanceof, ni lógica de negocio directa.
 * Cada responsabilidad está delegada a su patrón correspondiente.
 */
@Service
public class CreatePolicyUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final PolicyRepositoryPort policyRepository;
    private final PolicyFactoryRegistry factoryRegistry;
    private final RatingStrategyRegistry strategyRegistry;
    private final ObjectFactory<PolicyBuilder> policyBuilderFactory;
    private final PolicyNumberSequencer policyNumberSequencer;
    private final PolicyDtoMapper policyDtoMapper; // ← Declaramos la dependencia del Mapper

    public CreatePolicyUseCase(
            CustomerRepositoryPort customerRepository,
            PolicyRepositoryPort policyRepository,
            PolicyFactoryRegistry factoryRegistry,
            RatingStrategyRegistry strategyRegistry,
            ObjectFactory<PolicyBuilder> policyBuilderFactory,
            PolicyNumberSequencer policyNumberSequencer,
            PolicyDtoMapper policyDtoMapper) { // ← Lo inyectamos por constructor
        this.customerRepository = customerRepository;
        this.policyRepository = policyRepository;
        this.factoryRegistry = factoryRegistry;
        this.strategyRegistry = strategyRegistry;
        this.policyBuilderFactory = policyBuilderFactory;
        this.policyNumberSequencer = policyNumberSequencer;
        this.policyDtoMapper = policyDtoMapper;
    }

    public PolicyResponseDto execute(CreatePolicyDto dto) {
        // 1. Validar que el cliente existe y está activo
        var customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(dto.customerId()));

        if (!customer.isActive()) {
            throw new IllegalStateException(
                    "El cliente con id " + dto.customerId() + " no está activo"
            );
        }

        // 2. Factory Method — obtener cobertura y prima base según ramo
        PolicyFactoryPort factory = factoryRegistry.getFactory(dto.branch());
        Coverage coverage = factory.createDefaultCoverage();
        BigDecimal basePremium = factory.getBasePremium();

        // 3. Construir RiskProfile con los datos del request
        RiskProfile riskProfile = RiskProfile.of(dto.riskScore(), dto.customerSince());

        // 4. Strategy — validar riskProfile y calcular prima final
        RatingStrategyPort strategy = strategyRegistry.getStrategy(dto.ratingStrategy());
        strategy.validate(riskProfile);
        BigDecimal monthlyPremium = strategy.calculatePremium(basePremium, riskProfile);

        // 5. Builder — Obtener instancia fresca del Prototype y ensamblar
        Policy policy = policyBuilderFactory.getObject()
                .id(UUID.randomUUID())
                .policyNumber(policyNumberSequencer.next())
                .customerId(dto.customerId())
                .branch(dto.branch())
                .ratingStrategy(dto.ratingStrategy())
                .coverage(coverage)
                .monthlyPremium(monthlyPremium)
                .riskProfile(riskProfile)
                .build();

        // 6. Persistir
        Policy saved = policyRepository.save(policy);

        // 7. Mapear usando el componente transversal
        return policyDtoMapper.toResponse(saved);
    }
}