package com.insurance_api.policies.application.usecases;

import com.insurance_api.policies.application.dtos.ChangePolicyStatusDto;
import com.insurance_api.policies.application.dtos.PolicyResponseDto;
import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.exceptions.PolicyNotFoundException;
import com.insurance_api.policies.domain.models.Policy;
import com.insurance_api.policies.domain.ports.EventPublisherPort;
import com.insurance_api.policies.domain.ports.PolicyRepositoryPort;
import com.insurance_api.policies.domain.ports.PolicyStatePort;
import com.insurance_api.policies.domain.states.PolicyStateRegistry;
import com.insurance_api.policies.application.dtos.PolicyDtoMapper;
import com.insurance_api.shared.events.PolicyDomainEvent;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Orquesta el cambio de estado de una póliza.
 *
 * - NO contiene la matriz de transiciones — delega en el estado actual.
 * - NO contiene switch para reglas de negocio — el estado concreto decide si la transición es válida.
 * - Publica el evento de dominio correspondiente tras cada transición exitosa.
 */
@Service
public class ChangePolicyStatusUseCase {

    private final PolicyRepositoryPort policyRepository;
    private final PolicyStateRegistry stateRegistry;
    private final EventPublisherPort eventPublisher;
    private final PolicyDtoMapper policyDtoMapper; // ← 1. Declaramos el Mapper

    public ChangePolicyStatusUseCase(
            PolicyRepositoryPort policyRepository,
            PolicyStateRegistry stateRegistry,
            EventPublisherPort eventPublisher,
            PolicyDtoMapper policyDtoMapper) { // ← 2. Lo inyectamos
        this.policyRepository = policyRepository;
        this.stateRegistry = stateRegistry;
        this.eventPublisher = eventPublisher;
        this.policyDtoMapper = policyDtoMapper;
    }

    public PolicyResponseDto execute(UUID policyId, ChangePolicyStatusDto dto) {
        // 1. Obtener la póliza
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new PolicyNotFoundException(policyId));

        PolicyStatus oldStatus = policy.getStatus();
        PolicyStatus targetStatus = dto.targetStatus();

        // 2. Resolver el estado actual y delegar la transición (Patrón State)
        PolicyStatePort currentState = stateRegistry.getState(oldStatus);
        currentState.transitionTo(policy, targetStatus);

        // 3. Si fue idempotente (mismo estado) no persistir ni publicar
        if (oldStatus == policy.getStatus()) {
            return policyDtoMapper.toResponse(policy); // ← 3. Usamos el Mapper
        }

        // 4. Persistir el nuevo estado
        Policy saved = policyRepository.save(policy);

        // 5. Publicar evento de dominio al broker
        eventPublisher.publish(buildEvent(saved, oldStatus));

        return policyDtoMapper.toResponse(saved); // ← 4. Usamos el Mapper
    }

    // ── Construcción del evento según la transición ──
    private PolicyDomainEvent buildEvent(Policy policy, PolicyStatus oldStatus) {
        String eventType = resolveEventType(oldStatus, policy.getStatus());

        return PolicyDomainEvent.of(
                eventType,
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getCustomerId(),
                policy.getBranch(),
                oldStatus,
                policy.getStatus()
        );
    }

    /**
     * Resuelve el nombre del evento de infraestructura.
     * Nota: Estos ifs están permitidos porque no son lógica de negocio,
     * sino un simple mapeo de nombres para el tópico de mensajería.
     */
    private String resolveEventType(PolicyStatus from, PolicyStatus to) {
        if (to == PolicyStatus.ISSUED) return "policy.issued";
        if (to == PolicyStatus.ACTIVE && from == PolicyStatus.ISSUED) return "policy.activated";
        if (to == PolicyStatus.ACTIVE && from == PolicyStatus.SUSPENDED) return "policy.reactivated";
        if (to == PolicyStatus.SUSPENDED) return "policy.suspended";
        if (to == PolicyStatus.CANCELLED) return "policy.cancelled";
        return "policy.updated";
    }
}
