package com.insurance_api.audit.application.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.insurance_api.shared.events.PolicyDomainEvent;
import com.insurance_api.shared.events.PolicyEventTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer 2 — Auditoría.
 * Responsabilidad única: registrar trazas de auditoría de cada transición.
 * Completamente desacoplado del publisher y de NotificationsConsumer.

 * En producción aquí iría: persistir en tabla audit_log,
 * enviar a ELK / CloudWatch / Datadog, etc.
 * Por ahora registra trazas estructuradas con todos los datos del evento.
 */
@Component
public class AuditConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditConsumer.class);

    private final ObjectMapper objectMapper;

    public AuditConsumer() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @KafkaListener(
            topics = {
                    PolicyEventTopics.POLICY_ISSUED,
                    PolicyEventTopics.POLICY_ACTIVATED,
                    PolicyEventTopics.POLICY_SUSPENDED,
                    PolicyEventTopics.POLICY_REACTIVATED,
                    PolicyEventTopics.POLICY_CANCELLED
            },
            groupId = "audit-group"
    )
    public void consume(String message) {
        try {
            PolicyDomainEvent event = objectMapper.readValue(message, PolicyDomainEvent.class);
            logAuditTrace(event);

        } catch (Exception ex) {
            log.error("[AUDITORÍA] Error procesando evento: {}", ex.getMessage());
        }
    }

    private void logAuditTrace(PolicyDomainEvent event) {
        log.info("""
                [AUDITORÍA] ──────────────────────────────────
                  Evento       : {}
                  Póliza ID    : {}
                  Número       : {}
                  Cliente ID   : {}
                  Ramo         : {}
                  Estado prev. : {}
                  Estado nuevo : {}
                  Timestamp    : {}
                ──────────────────────────────────────────────
                """,
                event.eventType(),
                event.policyId(),
                event.policyNumber(),
                event.customerId(),
                event.branch(),
                event.oldStatus(),
                event.newStatus(),
                event.timestamp()
        );
    }
}
