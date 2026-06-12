package com.insurance_api.policies.domain.exceptions;

public class UnsupportedRatingStrategyException extends RuntimeException {
    public UnsupportedRatingStrategyException(String strategy) {
        super("Estrategia de tarificación no soportada: " + strategy);
    }
}
