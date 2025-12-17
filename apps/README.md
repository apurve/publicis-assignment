# Verification Guide

This document outlines the steps to verify the functionality of the Microservices Booking System, specifically focusing on the Kafka integration between the Catalog Service and Booking Service.

## 1. Infrastructure Status

Ensure all infrastructure components are running:

- **Kafka**: Port `9093` (External), `9092` (Internal)
- **Zookeeper**: Port `2181`
- **Redis**: Port `6379`
- **Eureka Server**: Port `8761` (`http://localhost:8761`)
- **Config Server**: Port `8888` (`http://localhost:8888/swagger-ui/index.html`)

## 2. Service Status

Ensure application services are running and registered with Eureka:

- **Catalog Service**: Port `8081` (`http://localhost:8081/swagger-ui/index.html`)
- **Booking Service**: Port `8080` (`http://localhost:8080/swagger-ui/index.html`)
- **Notification Service**: Port `8082` (`http://localhost:8082/swagger-ui/index.html`)

## 3. Redis Caching Verification (Catalog Service)

This test verifies that the Catalog Service correctly caches service data in Redis.

### Step 3.1: First Request (Cache Miss)

```bash
curl http://localhost:8081/api/catalog/services
```

**Expected Catalog Service Logs:**
```text
INFO ... [nio-8081-exec-1] c.e.c.service.CatalogDataService : Generating services (cache miss)
```

### Step 3.2: Second Request (Cache Hit)

```bash
curl http://localhost:8081/api/catalog/services
```

**Expected Catalog Service Logs:**
```text
INFO ... [nio-8081-exec-2] c.e.c.service.CatalogDataService : Fetching services from Redis cache
```

### Step 3.3: Verify Redis Storage

Check that data is stored in Redis:

```bash
# Check if key exists
docker exec infrastructure-redis-1 redis-cli GET catalog:services

# Check TTL (should be ~600 seconds or less)
docker exec infrastructure-redis-1 redis-cli TTL catalog:services
```

**Expected Response:** JSON data with 8 services (Gym, Pool, Tennis, Hall, Plumbing, Electrical, Cleaning, Pest Control)

## 4. End-to-End Verification (Booking Flow)

This test verifies that a booking request initiated in the Catalog Service is successfully published to Kafka and consumed by the Booking Service.

### Step 4.1: Trigger Booking Request

Send a POST request to the Catalog Service:

```bash
curl -X POST http://localhost:8081/api/catalog/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "serviceId": "GYM",
    "serviceType": "AMENITY",
    "startTime": "2025-12-02T10:00:00",
    "endTime": "2025-12-02T11:00:00"
  }'
```

**Expected Response:**
```json
{
  "message": "Booking request submitted",
  "status": "PENDING"
}
```

### Step 4.2: Verify Catalog Service Logs

Check the terminal running `catalog-service`. You should see logs indicating the message was sent:

```text
INFO ... [producer-1] c.e.c.producer.BookingProducer : Sending booking request to Kafka topic 'booking-requests': userId=1, serviceId=GYM
INFO ... [producer-1] c.e.c.producer.BookingProducer : Successfully sent booking request to Kafka
```

### Step 4.3: Verify Booking Service Logs

Check the terminal running `booking-service`. You should see logs indicating the message was received and processed:

```text
INFO ... [ntainer#0-0-C-1] c.e.b.consumer.BookingConsumer : Received booking request from Kafka: BookingRequest{userId=1, amenityId='GYM', startTime=2025-12-02T10:00, endTime=2025-12-02T11:00}
INFO ... [ntainer#0-0-C-1] c.e.b.consumer.BookingConsumer : Processing booking for user: 1, amenity: GYM
INFO ... [ntainer#0-0-C-1] c.e.b.consumer.BookingConsumer : Successfully processed booking for user: 1, amenity: GYM
```

### Step 4.4: Verify Notification Service (Reactive)

Check the terminal running `notification-service`. You should see logs indicating the notification was created and broadcast:

```text
INFO ... [parallel-1] c.e.n.c.BookingEventConsumer : Received Kafka message: key=null, partition=0, offset=8
INFO ... [parallel-1] c.e.n.c.BookingEventConsumer : Processing booking event: BookingEventDto{userId=1, serviceId='GYM', ...}
INFO ... [parallel-1] c.e.n.s.NotificationStreamService : 📡 Broadcast notification 1 to all subscribers
```

Alternatively, invoke the SSE endpoint **before** step 4.1 to see the notification in real-time:
```bash
curl -N http://localhost:8082/api/notifications/stream/user/1
```

## 5. Troubleshooting

### Common Issues

- **Connection Refused (Kafka)**: Ensure Kafka is running and accessible on port `9093`.
- **Connection Refused (Redis)**: Ensure Redis is running on port `6379` via `docker-compose up -d`.
- **Cache Not Working**: Check Redis logs with `docker logs infrastructure-redis-1`.

### Resetting State

If services get into a bad state:
1. Stop all Java applications.
2. Stop Docker containers: `cd apps/infrastructure && docker-compose down`
3. Start Docker containers: `docker-compose up -d`
4. Start services in order: Service Discovery → Config Server → Catalog/Booking Services.

### Clearing Redis Cache

To manually clear the catalog cache:
```bash
docker exec infrastructure-redis-1 redis-cli DEL catalog:services
```
