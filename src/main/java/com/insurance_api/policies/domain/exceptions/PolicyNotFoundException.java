package com.insurance_api.policies.domain.exceptions;

import java.util.UUID;

public class PolicyNotFoundException extends RuntimeException{
    public PolicyNotFoundException(UUID id) {
        super("Póliza no encontrada con id " + id);
    }
}
