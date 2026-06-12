package com.insurance_api.customers.infrastructure.controllers;

import com.insurance_api.customers.application.dtos.CreateCustomerDto;
import com.insurance_api.customers.application.dtos.CustomerResponseDto;
import com.insurance_api.customers.application.usecases.CreateCustomerUseCase;
import com.insurance_api.customers.application.usecases.FindCustomerUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Gestión de clientes")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final FindCustomerUseCase findCustomerUseCase;

    public CustomerController(CreateCustomerUseCase createCustomerUseCase,
                              FindCustomerUseCase findCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.findCustomerUseCase   = findCustomerUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear cliente")
    public CustomerResponseDto create(@Valid @RequestBody CreateCustomerDto dto) {
        return createCustomerUseCase.execute(dto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener cliente por ID")
    public CustomerResponseDto findById(@PathVariable UUID id) {
        return findCustomerUseCase.execute(id);
    }
}
