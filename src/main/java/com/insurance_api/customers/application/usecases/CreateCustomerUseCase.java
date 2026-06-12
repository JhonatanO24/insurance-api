package com.insurance_api.customers.application.usecases;

import com.insurance_api.customers.application.dtos.CreateCustomerDto;
import com.insurance_api.customers.application.dtos.CustomerResponseDto;
import com.insurance_api.customers.domain.exceptions.EmailAlreadyExistsException;
import com.insurance_api.customers.domain.models.Customer;
import com.insurance_api.customers.domain.ports.CustomerRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreateCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;

    public CreateCustomerUseCase(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponseDto execute(CreateCustomerDto dto) {
        if (customerRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        Customer customer = Customer.create(dto.name(), dto.email());
        Customer saved    = customerRepository.save(customer);

        return toResponse(saved);
    }

    private CustomerResponseDto toResponse(Customer customer) {
        return new CustomerResponseDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.isActive(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
