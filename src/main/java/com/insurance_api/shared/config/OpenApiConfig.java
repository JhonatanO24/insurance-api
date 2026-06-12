package com.insurance_api.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI insuranceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Insurance API — Pólizas de Seguros")
                        .description("""
                                API REST para la gestión del ciclo de vida de pólizas de seguros.
                                
                                ## Patrones de diseño implementados
                                - **Factory Method**: creación de pólizas por ramo (AUTO, LIFE, HOME, HEALTH)
                                - **Strategy**: tarificación de prima (STANDARD, RISK_BASED, LOYALTY)
                                - **Builder**: ensamblado fluido y validado del agregado Policy
                                - **State**: ciclo de vida QUOTED → ISSUED → ACTIVE → SUSPENDED → CANCELLED
                                - **Observer**: eventos de dominio publicados a Kafka
                                - **Singleton**: PolicyNumberSequencer para generación única de números
                                
                                ## Arquitectura
                                Hexagonal (Ports & Adapters) por módulo: customers, policies, notifications, audit
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Jhona")
                                .email(""))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor local de desarrollo")
                ));
    }
}
