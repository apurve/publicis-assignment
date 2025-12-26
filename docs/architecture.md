# System Architecture

## Microservices Interaction Diagram

The following diagram illustrates the architecture of the Apartment Management System, showing the interactions between microservices, infrastructure components, and external actors.

```mermaid
graph TB
    %% External Actors
    User([User / Client])

    %% Infrastructure Layer
    subgraph Infrastructure
        Kafka{Kafka Broker}
        Redis[(Redis Cache)]
        ZK[Zookeeper]
        Eureka[Service Discovery]
        Config[Config Server]
    end

    %% Microservices Layer
    subgraph Services
        Catalog[Catalog Service]
        Booking[Booking Service]
        Notification[Notification Service ⚡]
    end

    %% Databases
    subgraph Databases
        BookingDB[(H2 Booking DB)]
        NotifDB[(H2 Notification DB)]
    end

    %% Configuration & Discovery
    Config -->|Config| Catalog
    Config -->|Config| Booking
    Config -->|Config| Notification
    Config -->|Config| Eureka

    Catalog -->|Register| Eureka
    Booking -->|Register| Eureka
    Notification -->|Register| Eureka

    %% Main Flow
    User -->|1. Browse Services| Catalog
    Catalog -->|Cache Hit/Miss| Redis
    
    User -->|2. Create Booking| Catalog
    Catalog -->|3. Publish Event| Kafka
    
    Kafka -->|4. Consume Event| Booking
    Booking -->|5. Persist| BookingDB
    
    Kafka -->|4. Consume Event| Notification
    Notification -->|5. Persist| NotifDB
    
    %% Notification Flow
    Notification -->|6. SSE Stream| User
    
    %% Infrastructure Dependencies
    Kafka -.-> ZK
```

## Component Descriptions

| Component | Port | Description |
|-----------|------|-------------|
| **Config Server** | 8888 | Centralized configuration management. |
| **Service Discovery** | 8761 | Eureka server for service registration. |
| **Catalog Service** | 8081 | Manages service catalog, caches in Redis, publishes booking events. |
| **Booking Service** | 8080 | Consumes booking events, manages reservations (Blocking/JPA). |
| **Notification Service** | 8082 | Reactive service. Consumes events, streams notifications via SSE. |
| **Kafka** | 9093 | Message broker for asynchronous communication. |
| **Redis** | 6379 | In-memory cache for catalog data. |
