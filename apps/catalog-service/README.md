# Catalog Service

A microservice for browsing apartment services and initiating bookings via Kafka.

## Features

- **Service Browsing**: View 8 available services across amenities and repairs
  - **Amenities**: Gym, Swimming Pool, Tennis Court, Party Hall
  - **Repairs**: Plumbing, Electrical, Cleaning, Pest Control
- **Time Slot Availability**: Check available time slots for each service
- **Redis Caching**: Services are cached in Redis with 10-minute TTL for improved performance
- **Async Booking**: Submit booking requests that are processed asynchronously via Kafka
- **OpenAPI Documentation**: Interactive API docs at `/swagger-ui/index.html`

## API Endpoints

### GET /api/catalog/services
Returns list of all available services with time slots.

**Response Example:**
```json
[
  {
    "id": "GYM",
    "name": "Gym Session",
    "description": "Book a 1-hour gym session",
    "type": "AMENITY",
    "availableSlots": [
      {
        "slotId": "GYM-SLOT-1-AM",
        "startTime": "2025-12-03T06:00:00",
        "endTime": "2025-12-03T07:00:00",
        "available": true
      },
      {
        "slotId": "GYM-SLOT-1-PM",
        "startTime": "2025-12-03T18:00:00",
        "endTime": "2025-12-03T19:00:00",
        "available": true
      }
    ]
  },
  {
    "id": "POOL",
    "name": "Swimming Pool",
    "description": "Book a 1-hour swimming session",
    "type": "AMENITY",
    "availableSlots": [...]
  }
]
```

### POST /api/catalog/bookings
Submit a booking request (publishes to Kafka topic `booking-requests`).

**Request Body:**
```json
{
  "userId": 1,
  "serviceId": "GYM",
  "serviceType": "AMENITY",
  "startTime": "2025-12-03T10:00:00",
  "endTime": "2025-12-03T11:00:00"
}
```

**Response (202 Accepted):**
```json
{
  "message": "Booking request submitted",
  "status": "PENDING"
}
```

## Running the Service

The service is designed to run in **Kubernetes**.

1. **Deploy System**:
   ```bash
   # Using Terraform (Recommended)
   cd terraform && terraform apply
   
   # Using local script
   ./deploy_k8s_local.sh
   ```

2. **Access Locally**:
   ```bash
   kubectl port-forward svc/catalog-service 8081:8081
   ```

## Prerequisites

- **Infrastructure**: Kafka (9092) and Redis (6379) running in Kubernetes.

## Architecture

The catalog-service acts as the frontend for service discovery and booking initiation. It uses Redis for caching and Kafka as a message broker to decouple the user-facing API from the booking logic.

**Components:**
- **CatalogDataService**: Generates mock service data and manages Redis caching
- **CatalogController**: Exposes REST endpoints for browsing and booking
- **BookingProducer**: Publishes booking events to Kafka

**Flow:**
1. User browses services via `GET /api/catalog/services`
   - Service checks Redis cache first (key: `catalog:services`)
   - If cache miss, generates data and stores in Redis with 10-minute TTL
   - Returns list of 8 services with time slots
2. User submits booking via `POST /api/catalog/bookings`
3. Catalog service publishes event to `booking-requests` Kafka topic
4. Booking service consumes the event and processes the booking
5. Booking is persisted in the database

## Testing Redis Caching

```bash
# First request - should log "Generating services (cache miss)"
curl http://localhost:8081/api/catalog/services

# Second request - should log "Fetching services from Redis cache"
curl http://localhost:8081/api/catalog/services

# Check Redis directly (find redis pod name first)
kubectl exec deployment/redis -- redis-cli GET catalog:services
```

## Configuration

The service is configured via ConfigMap `catalog-config` mounted at `/app/config/application.yml`.

## Dependencies

Key dependencies (from `pom.xml`):
- Spring Boot Starter Web
- Spring Boot Starter Data Redis
- Spring Kafka
- SpringDoc OpenAPI
