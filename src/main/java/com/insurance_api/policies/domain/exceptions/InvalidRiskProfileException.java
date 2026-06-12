package com.insurance_api.policies.domain.exceptions;

public class InvalidRiskProfileException extends RuntimeException {
    public InvalidRiskProfileException(String message) {
        super(message);
    }
}
