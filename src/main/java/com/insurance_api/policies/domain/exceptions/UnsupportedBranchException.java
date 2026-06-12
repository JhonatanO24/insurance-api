package com.insurance_api.policies.domain.exceptions;

public class UnsupportedBranchException extends RuntimeException {
    public UnsupportedBranchException(String branch) {
        super("Ramo no soportado: " + branch);
    }
}
