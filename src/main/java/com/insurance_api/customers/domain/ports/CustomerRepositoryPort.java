package com.insurance_api.customers.domain.ports;

import com.insurance_api.customers.domain.models.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepositoryPort {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID id);
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
}
