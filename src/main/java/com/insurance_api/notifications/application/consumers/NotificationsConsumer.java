package com.insurance_api.notifications.application.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.insurance_api.shared.events.PolicyDomainEvent;
import com.insurance_api.shared.events.PolicyEventTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer 1 — Notificaciones.
 * Responsabilidad única: notificar al cliente sobre cambios en su póliza.
 * Completamente desacoplado del publisher — solo lee del broker.

 * En producción aquí iría: envío de email, SMS, push notification, etc.
 * Por ahora simula el envío con logs descriptivos.
 */
@Component
public class NotificationsConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationsConsumer.class);

    private final ObjectMapper objectMapper;

    public NotificationsConsumer() {
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
            groupId = "notifications-group"
    )
    public void consume(String message) {
        try {
            PolicyDomainEvent event = objectMapper.readValue(message, PolicyDomainEvent.class);
            String notification     = buildNotification(event);

            log.info("[NOTIFICACIONES] {}", notification);

        } catch (Exception ex) {
            log.error("[NOTIFICACIONES] Error procesando evento: {}", ex.getMessage());
        }
    }

    private String buildNotification(PolicyDomainEvent event) {
        return switch (event.eventType()) {
            case PolicyEventTopics.POLICY_ISSUED ->
                    "📋 Estimado cliente " + event.customerId() +
                            ", su póliza " + event.policyNumber() +
                            " de ramo " + event.branch() +
                            " ha sido EMITIDA exitosamente.";

            case PolicyEventTopics.POLICY_ACTIVATED ->
                    "✅ Estimado cliente " + event.customerId() +
                            ", su póliza " + event.policyNumber() +
                            " está ahora ACTIVA. ¡Ya tiene cobertura!";

            case PolicyEventTopics.POLICY_SUSPENDED ->
                    "⚠️ Estimado cliente " + event.customerId() +
                            ", su póliza " + event.policyNumber() +
                            " ha sido SUSPENDIDA temporalmente.";

            case PolicyEventTopics.POLICY_REACTIVATED ->
                    "🔄 Estimado cliente " + event.customerId() +
                            ", su póliza " + event.policyNumber() +
                            " ha sido REACTIVADA. ¡Cobertura restaurada!";

            case PolicyEventTopics.POLICY_CANCELLED ->
                    "❌ Estimado cliente " + event.customerId() +
                            ", su póliza " + event.policyNumber() +
                            " ha sido CANCELADA.";

            default -> "ℹ️ Actualización en póliza " + event.policyNumber();
        };
    }
}
