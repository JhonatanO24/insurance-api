package com.insurance_api.customers.domain.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Ya existe un cliente registrado con el email: " + email);
    }
}
