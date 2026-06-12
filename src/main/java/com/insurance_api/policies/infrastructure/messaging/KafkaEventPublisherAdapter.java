package com.insurance_api.policies.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.insurance_api.policies.domain.ports.EventPublisherPort;
import com.insurance_api.shared.events.PolicyDomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de infraestructura — implementa EventPublisherPort.
 * El dominio no sabe nada de Kafka — solo conoce el port.
 * Este adapter traduce el evento de dominio a un mensaje Kafka.
 */
@Component
public class KafkaEventPublisherAdapter implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisherAdapter.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaEventPublisherAdapter(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper  = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Override
    public void publish(PolicyDomainEvent event) {
        try {
            String topic   = event.eventType();
            String key     = event.policyId().toString();
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(topic, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("[Kafka] Error publicando evento {} para póliza {}: {}",
                                    event.eventType(), event.policyNumber(), ex.getMessage());
                        } else {
                            log.info("[Kafka] Evento publicado → topic: {} | póliza: {} | {} → {}",
                                    topic,
                                    event.policyNumber(),
                                    event.oldStatus(),
                                    event.newStatus());
                        }
                    });

        } catch (JsonProcessingException ex) {
            log.error("[Kafka] Error serializando evento {}: {}", event.eventType(), ex.getMessage());
            throw new RuntimeException("Error al serializar evento de dominio", ex);
        }
    }
}
