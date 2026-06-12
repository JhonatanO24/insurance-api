package com.insurance_api.customers.domain.exceptions;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(UUID id) {
        super("Cliente no encontrado con id: " + id);
    }
}
