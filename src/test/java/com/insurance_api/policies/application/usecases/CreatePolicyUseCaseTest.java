package com.insurance_api.policies.application.usecases;

import com.insurance_api.customers.domain.exceptions.CustomerNotFoundException;
import com.insurance_api.customers.domain.models.Customer;
import com.insurance_api.customers.domain.ports.CustomerRepositoryPort;
import com.insurance_api.policies.application.builders.PolicyBuilder;
import com.insurance_api.policies.application.dtos.CreatePolicyDto;
import com.insurance_api.policies.application.dtos.PolicyDtoMapper;
import com.insurance_api.policies.application.dtos.PolicyResponseDto;
import com.insurance_api.policies.application.factories.PolicyFactoryRegistry;
import com.insurance_api.policies.application.strategies.RatingStrategyRegistry;
import com.insurance_api.policies.domain.enums.Branch;
import com.insurance_api.policies.domain.enums.PolicyStatus;
import com.insurance_api.policies.domain.enums.RatingStrategy;
import com.insurance_api.policies.domain.models.Coverage;
import com.insurance_api.policies.domain.models.Policy;
import com.insurance_api.policies.domain.models.RiskProfile;
import com.insurance_api.policies.domain.ports.PolicyFactoryPort;
import com.insurance_api.policies.domain.ports.PolicyRepositoryPort;
import com.insurance_api.policies.domain.ports.RatingStrategyPort;
import com.insurance_api.shared.siingleton.PolicyNumberSequencer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreatePolicyUseCase — Tests unitarios")
class CreatePolicyUseCaseTest {

    @Mock
    private CustomerRepositoryPort customerRepository;

    @Mock
    private PolicyRepositoryPort policyRepository;

    @Mock
    private PolicyFactoryRegistry factoryRegistry;

    @Mock
    private RatingStrategyRegistry strategyRegistry;

    @Mock
    private PolicyBuilder policyBuilder;

    @Mock
    private PolicyNumberSequencer policyNumberSequencer;

    @Mock
    private PolicyFactoryPort policyFactory;

    @Mock
    private RatingStrategyPort ratingStrategy;

    @Mock
    private ObjectFactory<PolicyBuilder> policyBuilderFactory;

    @Mock
    private PolicyDtoMapper policyDtoMapper;

    @InjectMocks
    private CreatePolicyUseCase useCase;

    private UUID customerId;
    private Customer activeCustomer;
    private Coverage defaultCoverage;
    private Policy savedPolicy;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();

        activeCustomer = Customer.create("Juan Pérez", "juan@email.com");

        defaultCoverage = Coverage.builder()
                .coverageAmount(new BigDecimal("80000000"))
                .deductible(new BigDecimal("1000000"))
                .termMonths(12)
                .build();

        savedPolicy = new Policy(
                UUID.randomUUID(),
                "POL-2026-000001",
                customerId,
                Branch.AUTO,
                RatingStrategy.STANDARD,
                PolicyStatus.QUOTED,
                defaultCoverage,
                new BigDecimal("120000"),
                RiskProfile.empty(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("✅ AUTO + STANDARD → crea póliza con prima 120.000 en estado QUOTED")
    void shouldCreateAutoStandardPolicy() {
        // Arrange
        CreatePolicyDto dto = new CreatePolicyDto(
                customerId, Branch.AUTO, RatingStrategy.STANDARD, null, null
        );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(activeCustomer));
        when(factoryRegistry.getFactory(Branch.AUTO))
                .thenReturn(policyFactory);
        when(policyFactory.createDefaultCoverage())
                .thenReturn(defaultCoverage);
        when(policyFactory.getBasePremium())
                .thenReturn(new BigDecimal("120000"));
        when(strategyRegistry.getStrategy(RatingStrategy.STANDARD))
                .thenReturn(ratingStrategy);
        when(ratingStrategy.calculatePremium(any(), any()))
                .thenReturn(new BigDecimal("120000"));
        when(policyNumberSequencer.next())
                .thenReturn("POL-2026-000001");
        when(policyBuilder.id(any())).thenReturn(policyBuilder);
        when(policyBuilder.policyNumber(any())).thenReturn(policyBuilder);
        when(policyBuilder.customerId(any())).thenReturn(policyBuilder);
        when(policyBuilder.branch(any())).thenReturn(policyBuilder);
        when(policyBuilder.ratingStrategy(any())).thenReturn(policyBuilder);
        when(policyBuilder.coverage(any())).thenReturn(policyBuilder);
        when(policyBuilder.monthlyPremium(any())).thenReturn(policyBuilder);
        when(policyBuilder.riskProfile(any())).thenReturn(policyBuilder);
        when(policyBuilder.build()).thenReturn(savedPolicy);
        when(policyRepository.save(any())).thenReturn(savedPolicy);
        when(policyBuilderFactory.getObject()).thenReturn(policyBuilder);

        when(policyDtoMapper.toResponse(any())).thenReturn(
                new PolicyResponseDto(
                        savedPolicy.getId(),
                        savedPolicy.getPolicyNumber(),
                        savedPolicy.getCustomerId(),
                        savedPolicy.getBranch(),
                        savedPolicy.getRatingStrategy(),
                        savedPolicy.getStatus(),
                        null,
                        savedPolicy.getMonthlyPremium(),
                        null,
                        null,
                        null,
                        null
                )
        );

        // Act
        PolicyResponseDto response = useCase.execute(dto);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(PolicyStatus.QUOTED);
        assertThat(response.branch()).isEqualTo(Branch.AUTO);
        assertThat(response.monthlyPremium()).isEqualByComparingTo("120000");
        assertThat(response.policyNumber()).isEqualTo("POL-2026-000001");

        verify(policyRepository, times(1)).save(any());
        verify(ratingStrategy, times(1)).validate(any());
    }

    @Test
    @DisplayName("❌ Cliente inexistente → lanza CustomerNotFoundException")
    void shouldThrowWhenCustomerNotFound() {
        // Arrange
        CreatePolicyDto dto = new CreatePolicyDto(
                customerId, Branch.AUTO, RatingStrategy.STANDARD, null, null
        );
        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(dto))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining(customerId.toString());

        verify(policyRepository, never()).save(any());
    }

    @Test
    @DisplayName("❌ Cliente inactivo → lanza IllegalStateException")
    void shouldThrowWhenCustomerInactive() {
        // Arrange
        Customer inactiveCustomer = new Customer(
                customerId, "Juan", "juan@email.com",
                false, LocalDateTime.now(), LocalDateTime.now()
        );
        CreatePolicyDto dto = new CreatePolicyDto(
                customerId, Branch.AUTO, RatingStrategy.STANDARD, null, null
        );
        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(inactiveCustomer));

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no está activo");

        verify(policyRepository, never()).save(any());
    }
}
