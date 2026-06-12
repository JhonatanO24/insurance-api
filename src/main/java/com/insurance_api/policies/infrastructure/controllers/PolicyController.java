package com.insurance_api.policies.infrastructure.controllers;

import com.insurance_api.policies.application.dtos.ChangePolicyStatusDto;
import com.insurance_api.policies.application.dtos.CreatePolicyDto;
import com.insurance_api.policies.application.dtos.PolicyResponseDto;
import com.insurance_api.policies.application.usecases.ChangePolicyStatusUseCase;
import com.insurance_api.policies.application.usecases.CreatePolicyUseCase;
import com.insurance_api.policies.application.usecases.FindCustomerPoliciesUseCase;
import com.insurance_api.policies.application.usecases.FindPolicyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller de pólizas.
 * Responsabilidad única: traducir HTTP ↔ use cases.
 * Sin lógica de negocio — todo delega en los use cases.
 */
@RestController
@RequestMapping("/api/policies")
@Tag(name = "Policies", description = "Gestión del ciclo de vida de pólizas de seguros")
public class PolicyController {

    private final CreatePolicyUseCase createPolicyUseCase;
    private final FindPolicyUseCase findPolicyUseCase;
    private final FindCustomerPoliciesUseCase findCustomerPoliciesUseCase;
    private final ChangePolicyStatusUseCase changePolicyStatusUseCase;

    public PolicyController(
            CreatePolicyUseCase         createPolicyUseCase,
            FindPolicyUseCase           findPolicyUseCase,
            FindCustomerPoliciesUseCase findCustomerPoliciesUseCase,
            ChangePolicyStatusUseCase   changePolicyStatusUseCase) {
        this.createPolicyUseCase         = createPolicyUseCase;
        this.findPolicyUseCase           = findPolicyUseCase;
        this.findCustomerPoliciesUseCase = findCustomerPoliciesUseCase;
        this.changePolicyStatusUseCase   = changePolicyStatusUseCase;
    }

    //
    // POST /api/policies
    //
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary     = "Cotizar y crear póliza",
            description = "Crea una póliza en estado QUOTED aplicando " +
                    "Factory Method (cobertura por ramo), " +
                    "Strategy (cálculo de prima) y Builder (ensamblado validado)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Póliza creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o validación fallida",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "Número de póliza duplicado")
    })
    public PolicyResponseDto create(@Valid @RequestBody CreatePolicyDto dto) {
        return createPolicyUseCase.execute(dto);
    }

    //
    // GET /api/policies/:id
    //
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener póliza por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Póliza encontrada"),
            @ApiResponse(responseCode = "404", description = "Póliza no encontrada")
    })
    public PolicyResponseDto findById(
            @Parameter(description = "ID de la póliza", required = true)
            @PathVariable UUID id) {
        return findPolicyUseCase.execute(id);
    }

    //
    // GET /api/policies/customer/:id
    //
    @GetMapping("/customer/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener todas las pólizas de un cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pólizas del cliente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public List<PolicyResponseDto> findByCustomer(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable UUID customerId) {
        return findCustomerPoliciesUseCase.execute(customerId);
    }

    //
    // PATCH /api/policies/:id/status
    //
    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary     = "Cambiar estado de una póliza",
            description = "Transiciona el estado de la póliza validando " +
                    "las rutas permitidas por el patrón State. " +
                    "Publica evento de dominio al broker Kafka."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Transición de estado inválida"),
            @ApiResponse(responseCode = "404", description = "Póliza no encontrada")
    })
    public PolicyResponseDto changeStatus(
            @Parameter(description = "ID de la póliza", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody ChangePolicyStatusDto dto) {
        return changePolicyStatusUseCase.execute(id, dto);
    }
}
