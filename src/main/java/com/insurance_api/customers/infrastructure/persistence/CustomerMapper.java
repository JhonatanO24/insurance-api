package com.insurance_api.customers.infrastructure.persistence;

import com.insurance_api.customers.domain.models.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    // ORM → Dominio
    public Customer toDomain(CustomerEntity entity) {
        return new Customer(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // Dominio → ORM
    public CustomerEntity toEntity(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(customer.getId());
        entity.setName(customer.getName());
        entity.setEmail(customer.getEmail());
        entity.setActive(customer.isActive());
        entity.setCreatedAt(customer.getCreatedAt());
        entity.setUpdatedAt(customer.getUpdatedAt());
        return entity;
    }
}
