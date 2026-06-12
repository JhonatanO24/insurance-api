package com.insurance_api.customers.domain.models;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Customer {

    private final UUID id;
    private String name;
    private String email;
    private boolean isActive; //primitivo
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor completo (usado por el Mapper al reconstruir desde BD)
    public Customer(UUID id, String name, String email,
                    boolean isActive, LocalDateTime createdAt,
                    LocalDateTime updatedAt) {
        this.id        = id;
        this.name      = name;
        this.email     = email;
        this.isActive  = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Factory method de creación (nuevo cliente)
    public static Customer create(String name, String email) {
        return new Customer(
                UUID.randomUUID(),
                name,
                email,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
