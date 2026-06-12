package com.insurance_api.shared.config;

import com.insurance_api.shared.events.PolicyEventTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Crea los topics automáticamente al arrancar la app.
 * Sin esto Kafka los crea con configuración por defecto
 * o falla si auto.create.topics está deshabilitado.
 */
@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic policyIssuedTopic() {
        return TopicBuilder.name(PolicyEventTopics.POLICY_ISSUED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic policyActivatedTopic() {
        return TopicBuilder.name(PolicyEventTopics.POLICY_ACTIVATED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic policySuspendedTopic() {
        return TopicBuilder.name(PolicyEventTopics.POLICY_SUSPENDED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic policyReactivatedTopic() {
        return TopicBuilder.name(PolicyEventTopics.POLICY_REACTIVATED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic policyCancelledTopic() {
        return TopicBuilder.name(PolicyEventTopics.POLICY_CANCELLED)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
