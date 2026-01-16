# Microservices Applications

This directory contains the microservices for Apartment Management System.

## Modules

### 1. [Config Server](./config-server)
A centralized configuration management service using Spring Cloud Config.
- **Port**: 8888
- **Features**:
  - Native profile (local filesystem) configuration storage.
  - Swagger UI integration at `http://localhost:8888/swagger-ui/index.html`.
  - Registers with Service Discovery.

### 2. [Service Discovery](./service-discovery)
A service registry using Spring Cloud Netflix Eureka.
- **Port**: 8761
- **Features**:
  - Eureka Server for service registration and discovery.
  - Dashboard available at `http://localhost:8761`.

### 3. [Booking Service](./booking-service)
A booking and reservation management service for apartment amenities.
- **Port**: 8080
- **Features**:
  - REST API for creating bookings.
  - Business logic validation (maintenance fee, amenity availability).
  - H2 in-memory database.
  - **H2 Console** at `http://localhost:8080/h2-console`
  - Registers with Service Discovery.
  - **OpenAPI/Swagger UI** at `http://localhost:8080/swagger-ui/index.html`
  - OpenAPI spec at `http://localhost:8080/v3/api-docs`
  - **Kafka Consumer**: Processes booking events from `booking-requests` topic

### 4. [Catalog Service](./catalog-service)
A service catalog management system for browsing apartment services.
- **Port**: 8081
- **Features**:
  - Browse available services (amenities and repairs) with time slots.
  - **8 Services**: Gym, Swimming Pool, Tennis Court, Party Hall, Plumbing, Electrical, Cleaning, Pest Control.
  - **Redis Caching**: Services are cached in Redis with 10-minute TTL for improved performance.
  - Initiate booking requests via Kafka.
  - **OpenAPI/Swagger UI** at `http://localhost:8081/swagger-ui/index.html`
  - **Kafka Producer**: Publishes booking events to Kafka

### 5. [Notification Service](./notification-service) ⚡ **Reactive**
A reactive notification service using Spring WebFlux for real-time notifications.
- **Port**: 8082
- **Features**:
  - **Spring WebFlux**: Fully reactive, non-blocking REST API
  - **R2DBC**: Reactive database access with H2
  - **H2 Console** at `http://localhost:8082/h2-console`
  - **Reactor Kafka**: Reactive Kafka consumer for booking events
  - **Server-Sent Events (SSE)**: Real-time notification streaming
  - **Multi-channel**: Email, Push, SMS, In-App notifications (mock)
  - **OpenAPI/Swagger UI** at `http://localhost:8082/swagger-ui/index.html`
  - **Kafka Consumer**: Processes booking events reactively

### 6. [Infrastructure](./infrastructure)
Infrastructure configuration for Kafka, Zookeeper, and Redis.
- **Kafka Port**: 9093
- **Zookeeper Port**: 2181
- **Redis Port**: 6379

## Getting Started

### Prerequisites
- Java 17
- Maven

### Running the System

#### 1. Start Infrastructure (Kafka, Zookeeper, Redis)
```bash
cd apps/infrastructure
docker-compose up -d
```

#### 2. Start Service Discovery
```bash
cd apps/service-discovery
mvn spring-boot:run
```

#### 3. Start Config Server
```bash
cd apps/config-server
mvn spring-boot:run
```

#### 4. Start Catalog Service
```bash
cd apps/catalog-service
mvn spring-boot:run
```

#### 5. Start Booking Service
```bash
cd apps/booking-service
mvn spring-boot:run
```

#### 6. Start Notification Service (Reactive)
```bash
cd apps/notification-service
mvn spring-boot:run
```

### Testing the Services

**Catalog Service (with Redis caching):**
```bash
# First request - cache miss
curl http://localhost:8081/api/catalog/services

# Second request - cache hit (faster)
curl http://localhost:8081/api/catalog/services

# Check Redis
docker exec infrastructure-redis-1 redis-cli GET catalog:services
```

**Submit a booking:**
```bash
curl -X POST http://localhost:8081/api/catalog/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "serviceId": "GYM",
    "serviceType": "AMENITY",
    "startTime": "2025-12-03T10:00:00",
    "endTime": "2025-12-03T11:00:00"
  }'
```

**Test Reactive Notification Service (SSE):**
```bash
# Open SSE stream in one terminal
curl -N http://localhost:8082/api/notifications/stream/user/1

# In another terminal, create a booking to trigger notification
curl -X POST http://localhost:8081/api/catalog/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "serviceId": "GYM",
    "serviceType": "AMENITY",
    "startTime": "2025-12-17T10:00:00",
    "endTime": "2025-12-17T11:00:00"
  }'

# You should see the notification appear in real-time in the SSE stream!
```

**Check notifications for a user:**
```bash
# Get all notifications
curl http://localhost:8082/api/notifications/user/1

# Get unread count
curl http://localhost:8082/api/notifications/user/1/unread-count
```

## Architecture
- **Service Discovery** acts as the central registry.
- **Config Server** registers with Service Discovery and provides configuration to other services.
- **Catalog Service** uses Redis for caching service data and Kafka for async booking requests.
- **Booking Service** consumes booking requests from Kafka and persists to H2 database.
- **Notification Service** ⚡ reactive microservice using Spring WebFlux:
  - Consumes booking events from Kafka reactively (Reactor Kafka)
  - Stores notifications in H2 using R2DBC (reactive database)
  - Streams real-time notifications via Server-Sent Events (SSE)
  - Demonstrates reactive patterns: Mono, Flux, flatMap, hot publishers
- **Infrastructure** provides Kafka, Zookeeper, and Redis via Docker Compose.

## Technology Stack

### Traditional Services (Blocking)
- Spring Boot 3.2.0
- Spring Web MVC
- Spring Data JPA
- Spring Kafka

### Reactive Service (Non-blocking)
- Spring WebFlux
- Spring Data R2DBC
- Reactor Kafka
- Project Reactor
