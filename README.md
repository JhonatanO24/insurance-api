<h1 align="center">🛡️ Insurance API — Pólizas de Seguros 🛡️</h1>
 
<p align="center">
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
  <img src="https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Apache_Kafka-3.8-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white" alt="Kafka"/>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
  <img src="https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="Swagger"/>
  <img src="https://img.shields.io/badge/JaCoCo-Cobertura_70%25-C21325?style=for-the-badge&logo=jacoco&logoColor=white" alt="JaCoCo"/>
</p>
<p align="center">
  <img src="https://img.shields.io/badge/Arquitectura-Hexagonal-blueviolet?style=for-the-badge" alt="Hexagonal"/>
  <img src="https://img.shields.io/badge/Patrones-6_Design_Patterns-ff69b4?style=for-the-badge" alt="Patterns"/>
  <img src="https://img.shields.io/badge/SOLID-5_Principios-00c853?style=for-the-badge" alt="SOLID"/>
</p>
---
 
## 🎯 Objetivo del Proyecto
 
<em>
API REST para la gestión completa del ciclo de vida de pólizas de seguros.
El proyecto implementa arquitectura hexagonal (Ports & Adapters) por módulo,
seis patrones de diseño aplicados con criterio sobre un dominio real de seguros,
los cinco principios SOLID, código limpio, persistencia real con PostgreSQL,
mensajería asíncrona con Apache Kafka y documentación interactiva con Swagger.
</em>
> **Reto Integrador — Sofka **
 
---
 
## 🚀 Funcionalidades Principales
 
### 🏗️ Arquitectura Hexagonal (Ports & Adapters)
Cada módulo (`customers`, `policies`, `notifications`, `audit`) tiene sus propias
capas `domain/`, `application/` e `infrastructure/`. El dominio no importa ORM,
broker ni HTTP — está completamente aislado de la infraestructura.
 
### 🏭 Factory Method — Creación por Ramo
Cuatro factories concretas (`AUTO`, `LIFE`, `HOME`, `HEALTH`) que producen
la cobertura por defecto de cada ramo. El use case nunca decide con un `switch` —
todo se resuelve por registro automático de Spring.
 
### 💰 Strategy — Tarificación de Prima
Tres estrategias de pricing intercambiables (`STANDARD`, `RISK_BASED`, `LOYALTY`)
con validaciones específicas y cálculos exactos. Agregar una 4ª estrategia = una
clase nueva, cero cambios en el use case.
 
### 🔨 Builder — Ensamblado Fluido y Validado
`PolicyBuilder` con API fluida, validación de campos obligatorios en `build()` y
asignación automática del estado inicial `QUOTED`. Scope prototype para evitar
estado residual entre requests concurrentes.
 
### 🔄 State — Ciclo de Vida de la Póliza
Máquina de cinco estados (`QUOTED → ISSUED → ACTIVE → SUSPENDED → CANCELLED`)
donde cada estado encapsula sus transiciones válidas. El use case no contiene
ninguna matriz de transiciones — delega siempre en el estado actual.
 
### 📨 Observer — Eventos de Dominio con Kafka
En cada transición exitosa se publica un evento al broker. Dos consumers
completamente desacoplados (`NotificationsConsumer`, `AuditConsumer`) con
`groupId` distintos, garantizando que ambos reciban cada evento.
 
### 🔢 Singleton — PolicyNumberSequencer *(Bonus)*
Genera números de póliza únicos y secuenciales (`POL-2026-000001`).
Implementado como singleton del contenedor de DI de Spring con `AtomicLong`
para thread-safety sin bloqueos explícitos.
 
---
 
## 🛠️ Stack Tecnológico
 
| Capa | Tecnología |
|------|-----------|
| ☕ **Lenguaje** | Java 21 |
| 🍃 **Framework** | Spring Boot 4.0.6 |
| 🗄️ **Persistencia** | Spring Data JPA + Hibernate |
| 🐘 **Base de Datos** | PostgreSQL 16 |
| 📨 **Broker** | Apache Kafka + Zookeeper |
| 🖥️ **Kafka UI** | Provectus Kafka UI |
| ✈️ **Migraciones** | Flyway |
| 🌐 **Documentación** | SpringDoc OpenAPI (Swagger) |
| 🧪 **Testing** | JUnit 5 + Mockito + AssertJ |
| 📊 **Cobertura** | JaCoCo (umbral 70%) |
| 🐳 **Contenedores** | Docker + Docker Compose |
| ⚙️ **Build** | Maven |
| 🎨 **Boilerplate** | Lombok |
 
---
 
## 📁 Estructura del Proyecto
 
```plaintext
📦 com.insurance
├── 📂 customers/                      → 👤 Gestión de clientes
│   ├── 📂 domain/
│   │   ├── models/        Customer.java
│   │   ├── ports/         CustomerRepositoryPort.java
│   │   └── exceptions/    CustomerNotFoundException.java
│   │                      EmailAlreadyExistsException.java
│   ├── 📂 application/
│   │   ├── dtos/          CreateCustomerDto.java
│   │   │                  CustomerResponseDto.java
│   │   └── use-cases/     CreateCustomerUseCase.java
│   │                      FindCustomerUseCase.java
│   └── 📂 infrastructure/
│       ├── controllers/   CustomerController.java
│       └── persistence/   CustomerEntity.java
│                          CustomerJpaRepository.java
│                          CustomerMapper.java
│                          CustomerRepositoryAdapter.java
│
├── 📂 policies/                       → 🛡️ Ciclo de vida de pólizas
│   ├── 📂 domain/
│   │   ├── models/        Policy.java · Coverage.java · RiskProfile.java
│   │   ├── enums/         Branch.java · RatingStrategy.java · PolicyStatus.java
│   │   ├── ports/         PolicyRepositoryPort.java · PolicyFactoryPort.java
│   │   │                  RatingStrategyPort.java · PolicyStatePort.java
│   │   │                  EventPublisherPort.java
│   │   ├── states/        QuotedState.java · IssuedState.java · ActiveState.java
│   │   │                  SuspendedState.java · CancelledState.java
│   │   │                  PolicyStateRegistry.java
│   │   └── exceptions/    PolicyNotFoundException.java
│   │                      InvalidStateTransitionException.java
│   │                      UnsupportedBranchException.java
│   │                      UnsupportedRatingStrategyException.java
│   │                      InvalidRiskProfileException.java
│   ├── 📂 application/
│   │   ├── builders/      PolicyBuilder.java
│   │   ├── dtos/          CreatePolicyDto.java · PolicyResponseDto.java
│   │   │                  CoverageResponseDto.java · ChangePolicyStatusDto.java
│   │   ├── factories/     AutoPolicyFactory.java · LifePolicyFactory.java
│   │   │                  HomePolicyFactory.java · HealthPolicyFactory.java
│   │   │                  PolicyFactoryRegistry.java
│   │   ├── strategies/    StandardRatingStrategy.java · RiskBasedRatingStrategy.java
│   │   │                  LoyaltyRatingStrategy.java · RatingStrategyRegistry.java
│   │   └── use-cases/     CreatePolicyUseCase.java · FindPolicyUseCase.java
│   │                      FindCustomerPoliciesUseCase.java
│   │                      ChangePolicyStatusUseCase.java
│   └── 📂 infrastructure/
│       ├── controllers/   PolicyController.java
│       ├── persistence/   PolicyEntity.java · PolicyJpaRepository.java
│       │                  PolicyMapper.java · PolicyRepositoryAdapter.java
│       └── messaging/     KafkaEventPublisherAdapter.java
│
├── 📂 notifications/                  → 🔔 Consumer de notificaciones
│   └── application/consumers/        NotificationsConsumer.java
│
├── 📂 audit/                          → 📋 Consumer de auditoría
│   └── application/consumers/        AuditConsumer.java
│
└── 📂 shared/                         → 🔗 Componentes transversales
    ├── config/            OpenApiConfig.java · KafkaTopicConfig.java
    ├── events/            PolicyDomainEvent.java · PolicyEventTopics.java
    ├── exceptions/        GlobalExceptionHandler.java
    └── singleton/         PolicyNumberSequencer.java
```
 
---
 
## 🗺️ Mapa de Patrones de Diseño
 
| Patrón | Archivos principales | Dónde se aplica |
|--------|---------------------|-----------------|
| 🏭 **Factory Method** | `PolicyFactoryPort` · `AutoPolicyFactory` · `LifePolicyFactory` · `HomePolicyFactory` · `HealthPolicyFactory` · `PolicyFactoryRegistry` | `policies/domain/ports/` · `policies/application/factories/` |
| 💰 **Strategy** | `RatingStrategyPort` · `StandardRatingStrategy` · `RiskBasedRatingStrategy` · `LoyaltyRatingStrategy` · `RatingStrategyRegistry` | `policies/domain/ports/` · `policies/application/strategies/` |
| 🔨 **Builder** | `PolicyBuilder` | `policies/application/builders/` |
| 🔄 **State** | `PolicyStatePort` · `QuotedState` · `IssuedState` · `ActiveState` · `SuspendedState` · `CancelledState` · `PolicyStateRegistry` | `policies/domain/states/` |
| 📨 **Observer** | `EventPublisherPort` · `KafkaEventPublisherAdapter` · `NotificationsConsumer` · `AuditConsumer` | `policies/domain/ports/` · `policies/infrastructure/messaging/` · `notifications/` · `audit/` |
| 🔢 **Singleton** *(Bonus)* | `PolicyNumberSequencer` | `shared/singleton/` |
 
---
 
## 🔄 Ciclo de Vida de la Póliza
 
```
                    ┌─────────────────────────────────────┐
                    │            TODAS → CANCELLED         │
                    │                                      │
          ┌─────────▼──────────┐                          │
          │    QUOTED           │──────────────────────────┤
          └─────────┬──────────┘                          │
                    │ policy.issued                        │
          ┌─────────▼──────────┐                          │
          │    ISSUED           │──────────────────────────┤
          └─────────┬──────────┘                          │
                    │ policy.activated                     │
          ┌─────────▼──────────┐                          │
          │    ACTIVE           │◄──────────┐             │
          └─────────┬──────────┘           │             │
                    │ policy.suspended      │ policy       │
          ┌─────────▼──────────┐           │ .reactivated │
          │    SUSPENDED        │───────────┘             │
          └─────────┬──────────┘                          │
                    │                                      │
                    └──────────────────────────────────────┘
                                                    ▼
                                         ┌──────────────────┐
                                         │    CANCELLED      │
                                         │    (terminal)     │
                                         └──────────────────┘
```
 
---
 
## 💰 Tabla de Tarificación
 
| Ramo | Prima Base | STANDARD | RISK_BASED (score=50) | LOYALTY (≥2 años) |
|------|-----------|----------|----------------------|-------------------|
| AUTO | 120.000 | **120.000** | **180.000** | **102.000** |
| LIFE | 90.000 | **90.000** | **135.000** | **76.500** |
| HOME | 75.000 | **75.000** | **112.500** | **63.750** |
| HEALTH | 180.000 | **180.000** | **270.000** | **153.000** |
 
> Fórmulas: `STANDARD = base` · `RISK_BASED = base × (1 + score/100)` · `LOYALTY = base × 0.85`
 
---
 
## ⚙️ Instalación y Ejecución
 
### Prerequisitos
- Java 21+
- Docker Desktop
- Maven 3.9+
### 1️⃣ Clonar el repositorio
 
```bash
git clone https://github.com/JhonatanO24/insurance-api.git
cd insurance-api
```
 
### 2️⃣ Configurar variables de entorno
 
```bash
cp .env.example .env
# Editar .env si necesitas cambiar puertos o credenciales
```
 
### 3️⃣ Levantar infraestructura (PostgreSQL + Kafka)
 
```bash
docker-compose up -d
```
 
Verificar que todos los servicios estén saludables:
 
```bash
docker-compose ps
```
 
### 4️⃣ Ejecutar la aplicación
 
```bash
./mvnw spring-boot:run
```
 
### 5️⃣ Verificar que todo está funcionando
 
| Servicio | URL |
|---------|-----|
| 🌐 **Swagger UI** | http://localhost:8080/api/docs |
| 📋 **API Docs JSON** | http://localhost:8080/api-docs |
| 🖥️ **Kafka UI** | http://localhost:8090 |
 
---
 
## 🧪 Tests y Cobertura
 
```bash
# Ejecutar todos los tests
mvn test
 
# Ejecutar tests + generar reporte JaCoCo
mvn verify
 
# Ver reporte de cobertura
# Abrir en el navegador: target/site/jacoco/index.html
```
 
Cobertura mínima configurada: **70%** sobre líneas de código.
El build falla automáticamente si no se alcanza el umbral.
 
---
 
## 📡 Endpoints de la API
 
| Método | Endpoint | Descripción | Código éxito |
|--------|----------|-------------|-------------|
| `POST` | `/api/customers` | Crear cliente | `201` |
| `GET` | `/api/customers/:id` | Obtener cliente por ID | `200` |
| `POST` | `/api/policies` | Cotizar y crear póliza | `201` |
| `GET` | `/api/policies/:id` | Obtener póliza por ID | `200` |
| `GET` | `/api/policies/customer/:id` | Pólizas de un cliente | `200` |
| `PATCH` | `/api/policies/:id/status` | Cambiar estado de póliza | `200` |
 
### Códigos de error
 
| Código | Cuándo |
|--------|--------|
| `400` | Validación fallida, transición inválida, riskProfile inválido |
| `404` | Cliente o póliza no encontrados |
| `409` | Email o número de póliza duplicado |
 
---
 
## 🔢 Patrón Singleton — Investigación y Aplicación
 
### ¿Qué recurso lo usa y por qué?
 
`PolicyNumberSequencer` genera números de póliza con formato
`POL-{AÑO}-{SECUENCIA}` (ej: `POL-2026-000001`).
 
Este recurso **genuinamente debe existir una sola vez** porque:
- El número de póliza tiene constraint `UNIQUE` en base de datos.
- Si dos instancias compitieran por el mismo contador bajo carga
  concurrente, podrían generar el mismo número causando errores
  intermitentes y difíciles de reproducir.
### ¿Cómo se garantiza la unicidad?
 
Se usa el **scope singleton del contenedor de DI de Spring** — no un
Singleton "a mano" con `getInstance()` estático:
 
```java
@Component  // Spring scope singleton por defecto = una sola instancia
public class PolicyNumberSequencer { ... }
```
 
| Singleton manual (`getInstance()`) | Singleton del contenedor (Spring) |
|------------------------------------|-----------------------------------|
| ❌ Rompe la testabilidad | ✅ Completamente mockeable en tests |
| ❌ Estado global estático | ✅ Ciclo de vida gestionado por Spring |
| ❌ Acoplamiento a la implementación | ✅ Inyectable por constructor |
 
### ¿Qué riesgo se mitigó?
 
El principal riesgo del Singleton es el **estado global mutable compartido
entre threads**. Se mitiga con `AtomicLong`:
 
```java
private final AtomicLong counter = new AtomicLong(0);
 
public String next() {
    long sequence = counter.incrementAndGet(); // CAS — atómico a nivel hardware
    ...
}
```
 
`incrementAndGet()` usa la instrucción **Compare And Swap (CAS)** del
procesador — thread-safe sin bloques `synchronized` que degradarían el
rendimiento bajo carga concurrente.
 
---
 
## 📊 Principios SOLID aplicados
 
| Principio | Dónde se evidencia |
|-----------|-------------------|
| **S** — Single Responsibility | Factory ≠ Strategy ≠ State ≠ UseCase ≠ Mapper — cada clase tiene una razón de cambio |
| **O** — Open/Closed | Agregar ramo o estrategia = clase nueva + `@Component`, sin tocar use cases |
| **L** — Liskov Substitution | Cualquier `PolicyFactoryPort` / `RatingStrategyPort` / `PolicyStatePort` es sustituible |
| **I** — Interface Segregation | Ports pequeños y cohesionados — ningún implementador tiene métodos vacíos |
| **D** — Dependency Inversion | Use cases dependen de abstracciones `*Port` inyectadas — sin `new` |
 
---
 
## 🏗️ Decisiones de Arquitectura
 
**¿Por qué Spring Boot 4.0.6?**
Es la versión estable actual (junio 2026), construida sobre Spring Framework 7
con Jakarta EE 11 y soporte de primera clase para Java 21.
 
**¿Por qué PostgreSQL?**
Soporte nativo de `JSONB` para persistir los Value Objects `Coverage` y
`RiskProfile` sin necesidad de tablas adicionales, manteniendo flexibilidad
por ramo sin romper el esquema relacional.
 
**¿Por qué Flyway?**
Hibernate solo valida el schema (`ddl-auto: validate`). Flyway lo crea y
evoluciona de forma versionada, reproducible y auditable.
 
**¿Por qué el Mapper manual en vez de MapStruct?**
Los Value Objects (`Coverage`, `RiskProfile`) requieren lógica de
serialización/deserialización desde `Map<String, Object>` (JSONB) que
MapStruct no maneja sin configuración compleja. El mapper manual es
más explícito y fácil de auditar.
 
---
 
## 🔔 Eventos de Dominio — Kafka Topics
 
| Topic | Transición | Consumer(s) |
|-------|-----------|-------------|
| `policy.issued` | QUOTED → ISSUED | Notifications · Audit |
| `policy.activated` | ISSUED → ACTIVE | Notifications · Audit |
| `policy.suspended` | ACTIVE → SUSPENDED | Notifications · Audit |
| `policy.reactivated` | SUSPENDED → ACTIVE | Notifications · Audit |
| `policy.cancelled` | * → CANCELLED | Notifications · Audit |
 
Payload de cada evento:
```json
{
  "eventType": "policy.activated",
  "policyId": "uuid",
  "policyNumber": "POL-2026-000001",
  "customerId": "uuid",
  "branch": "AUTO",
  "oldStatus": "ISSUED",
  "newStatus": "ACTIVE",
  "timestamp": "2026-06-08T14:30:00"
}
```
 
---
 
## 🤝 Convenciones de Commits
 
Este proyecto usa **Conventional Commits**:
 
```
feat:     nueva funcionalidad
fix:      corrección de bug
refactor: cambio que no agrega feature ni corrige bug
test:     agregar o modificar tests
docs:     cambios en documentación
chore:    tareas de mantenimiento
```
 
---
 
<p align="center">
  Desarrollado con 💙 y mucha dedicación por <strong>Jhona :3</strong>
  <br/>
  <em>Reto Integrador — Sofka · Junio 2026</em>
</p>
 
