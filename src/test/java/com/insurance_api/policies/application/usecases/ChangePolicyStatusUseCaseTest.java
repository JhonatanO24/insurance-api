package com.insurance_api.policies.application.usecases;

import com.insurance_api.policies.application.dtos.ChangePolicyStatusDto;
import com.insurance_api.policies.application.dtos.PolicyDtoMapper;
import com.insurance_api.policies.application.dtos.PolicyResponseDto;
import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.enums.RatingStrategy;
import com.insurance_api.policies.domain.exceptions.InvalidStateTransitionException;
import com.insurance_api.policies.domain.exceptions.PolicyNotFoundException;
import com.insurance_api.policies.domain.models.Coverage;
import com.insurance_api.policies.domain.models.Policy;
import com.insurance_api.policies.domain.models.RiskProfile;
import com.insurance_api.policies.domain.ports.EventPublisherPort;
import com.insurance_api.policies.domain.ports.PolicyRepositoryPort;
import com.insurance_api.policies.domain.states.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangePolicyStatusUseCase — Tests unitarios")
class ChangePolicyStatusUseCaseTest {

    @Mock
    private PolicyRepositoryPort policyRepository;

    @Mock
    private EventPublisherPort eventPublisher;

    @Mock
    private PolicyDtoMapper policyDtoMapper;

    private ChangePolicyStatusUseCase useCase;
    private PolicyStateRegistry stateRegistry;
    private UUID policyId;
    private Policy quotedPolicy;

    @BeforeEach
    void setUp() {
        // Construir el registry real con los 5 estados
        stateRegistry = new PolicyStateRegistry(List.of(
                new QuotedState(),
                new IssuedState(),
                new ActiveState(),
                new SuspendedState(),
                new CancelledState()
        ));

        useCase = new ChangePolicyStatusUseCase(
                policyRepository, stateRegistry, eventPublisher, policyDtoMapper
        );

        policyId = UUID.randomUUID();

        Coverage coverage = Coverage.builder()
                .coverageAmount(new BigDecimal("80000000"))
                .termMonths(12)
                .build();

        quotedPolicy = new Policy(
                policyId, "POL-2026-000001", UUID.randomUUID(),
                Branch.AUTO, RatingStrategy.STANDARD, PolicyStatus.QUOTED,
                coverage, new BigDecimal("120000"), RiskProfile.empty(),
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("✅ QUOTED → ISSUED transición válida")
    void shouldTransitionFromQuotedToIssued() {
        when(policyRepository.findById(policyId))
                .thenReturn(Optional.of(quotedPolicy));
        when(policyRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        when(policyDtoMapper.toResponse(any()))
                .thenReturn(new PolicyResponseDto(policyId, "POL-2026-000001", UUID.randomUUID(),
                        null, null, PolicyStatus.ISSUED, null, null, null, null, null, null));

        PolicyResponseDto response = useCase.execute(
                policyId, new ChangePolicyStatusDto(PolicyStatus.ISSUED)
        );

        assertThat(response.status()).isEqualTo(PolicyStatus.ISSUED);
        verify(eventPublisher, times(1)).publish(any());
    }

    @Test
    @DisplayName("❌ QUOTED → ACTIVE transición inválida → 400")
    void shouldThrowOnInvalidTransition() {
        when(policyRepository.findById(policyId))
                .thenReturn(Optional.of(quotedPolicy));

        assertThatThrownBy(() -> useCase.execute(
                policyId, new ChangePolicyStatusDto(PolicyStatus.ACTIVE)
        ))
                .isInstanceOf(InvalidStateTransitionException.class)
                .hasMessageContaining("QUOTED")
                .hasMessageContaining("ACTIVE");

        verify(eventPublisher, never()).publish(any());
        verify(policyRepository, never()).save(any());
    }

    @Test
    @DisplayName("❌ Póliza inexistente → PolicyNotFoundException")
    void shouldThrowWhenPolicyNotFound() {
        when(policyRepository.findById(policyId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                policyId, new ChangePolicyStatusDto(PolicyStatus.ISSUED)
        ))
                .isInstanceOf(PolicyNotFoundException.class);
    }

    @Test
    @DisplayName("✅ Transición idempotente — mismo estado no persiste ni publica")
    void shouldBeIdempotentOnSameStatus() {
        // 1. Arrange
        when(policyRepository.findById(policyId))
                .thenReturn(Optional.of(quotedPolicy));

        // ¡Aquí faltaba esto! Configuramos qué devolver cuando se mapea el DTO
        when(policyDtoMapper.toResponse(any()))
                .thenReturn(new PolicyResponseDto(policyId, "POL-2026-000001", null, null, null, PolicyStatus.QUOTED, null, null, null, null, null, null));

        // 2. Act
        PolicyResponseDto response = useCase.execute(
                policyId, new ChangePolicyStatusDto(PolicyStatus.QUOTED)
        );

        // 3. Assert
        assertThat(response.status()).isEqualTo(PolicyStatus.QUOTED);
        verify(policyRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("❌ CANCELLED → cualquier estado → siempre 400")
    void shouldThrowFromCancelledToAnything() {
        Coverage coverage = Coverage.builder()
                .coverageAmount(new BigDecimal("80000000")).build();

        Policy cancelledPolicy = new Policy(
                policyId, "POL-2026-000002", UUID.randomUUID(),
                Branch.AUTO, RatingStrategy.STANDARD, PolicyStatus.CANCELLED,
                coverage, new BigDecimal("120000"), RiskProfile.empty(),
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(policyRepository.findById(policyId))
                .thenReturn(Optional.of(cancelledPolicy));

        assertThatThrownBy(() -> useCase.execute(
                policyId, new ChangePolicyStatusDto(PolicyStatus.ACTIVE)
        ))
                .isInstanceOf(InvalidStateTransitionException.class)
                .hasMessageContaining("CANCELLED");
    }
}
