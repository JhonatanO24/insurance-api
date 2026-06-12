package com.insurance_api.policies.domain.ports;

import com.insurance_api.shared.events.PolicyDomainEvent;

/**
 * Puerto de salida para publicación de eventos de dominio.
 * Observer: el dominio no conoce Kafka - solo este contrato.
 */
public interface EventPublisherPort {
    void publish(PolicyDomainEvent event);
}
