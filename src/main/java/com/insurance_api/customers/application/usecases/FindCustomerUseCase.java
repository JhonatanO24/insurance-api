package com.insurance_api.customers.application.usecases;

import com.insurance_api.customers.application.dtos.CustomerResponseDto;
import com.insurance_api.customers.domain.exceptions.CustomerNotFoundException;
import com.insurance_api.customers.domain.ports.CustomerRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FindCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;

    public FindCustomerUseCase(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponseDto execute(UUID id) {
        return customerRepository.findById(id)
                .map(customer -> new CustomerResponseDto(
                        customer.getId(),
                        customer.getName(),
                        customer.getEmail(),
                        customer.isActive(),
                        customer.getCreatedAt(),
                        customer.getUpdatedAt()
                ))
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }
}
