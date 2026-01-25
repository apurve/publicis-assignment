# Notification Service (Reactive) 🚀

A reactive microservice built with **Spring WebFlux** for handling real-time notifications in the Apartment Management System.

## 🎯 Learning Goals

This service demonstrates **Spring Reactive Programming** concepts:
- ✅ **Mono & Flux** - Core reactive types
- ✅ **WebFlux** - Non-blocking REST APIs  
- ✅ **R2DBC** - Reactive database access
- ✅ **Reactor Kafka** - Reactive event streaming
- ✅ **Server-Sent Events (SSE)** - Real-time server push
- ✅ **WebClient** - Non-blocking HTTP calls
- ✅ **Hot Publishers** - Shared notification streams
- ✅ **Backpressure** - Handling slow consumers

---

## 📋 Features

- **Reactive Kafka Consumer** - Consumes booking events from Kafka
- **Multi-channel Notifications** - Email, Push, SMS, In-App
- **Real-time Streaming** - Server-Sent Events for live updates
- **Non-blocking Database** - R2DBC with H2
- **OpenAPI Documentation** - Swagger UI

---

## 🏗️ Architecture

```
Kafka (booking-requests)
    ↓ (reactive consumer)
Notification Service (WebFlux)
    ├→ R2DBC (H2) - Persist notifications
    ├→ Email Service (mock)
    ├→ Push Service (mock)
    └→ SSE Stream - Real-time to clients
```

---

## 🚀 Quick Start

### 1. Start System

```bash
./deploy_k8s.sh
```

### 2. Access Logs & Service

```bash
# View Logs
kubectl logs deployment/notification-service -f

# Local Access
kubectl port-forward svc/notification-service 8082:8082
```

The service listens on **port 8082**.

---

## 🔥 Key Reactive Patterns

### 1. Reactive Repository (R2DBC)

```java
// Returns Flux<Notification> instead of List<Notification>
public interface NotificationRepository extends ReactiveCrudRepository<Notification, Long> {
    Flux<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    Mono<Long> countUnreadByUserId(Long userId);
}
```

### 2. Reactive Kafka Consumer

```java
@PostConstruct
public void startConsuming() {
    kafkaReceiver.receive()  // Returns Flux<ReceiverRecord>
            .flatMap(this::processBookingEvent)  // Async processing
            .subscribe();  // Activate stream
}
```

### 3. WebFlux Controller

```java
// Returns Mono instead of ResponseEntity
@GetMapping("/user/{userId}")
public Mono<List<NotificationDto>> getUserNotifications(@PathVariable Long userId) {
    return notificationService.getUserNotifications(userId)
            .map(notificationService::toDto)
            .collectList();
}
```

### 4. Server-Sent Events (SSE)

```java
@GetMapping(value = "/stream/user/{userId}", 
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<NotificationDto> streamNotifications(@PathVariable Long userId) {
    return streamService.getNotificationStream(userId)
            .map(notificationService::toDto);
}
```

---

## 📡 API Endpoints

### Swagger UI
```
http://localhost:8082/swagger-ui/index.html
```

### REST Endpoints

| Method | Endpoint | Description | Returns |
|--------|----------|-------------|---------|
| GET | `/api/notifications/user/{userId}` | Get all notifications | `Mono<List<NotificationDto>>` |
| GET | `/api/notifications/user/{userId}/unread` | Get unread notifications | `Mono<List<NotificationDto>>` |
| GET | `/api/notifications/user/{userId}/unread-count` | Get unread count | `Mono<Map>` |
| PATCH | `/api/notifications/{id}/read` | Mark as read | `Mono<NotificationDto>` |
| GET | `/api/notifications/stream/user/{userId}` | **SSE Stream** | `Flux<NotificationDto>` |
| PATCH | `/api/notifications/{id}/read` | Mark as read | `Mono<NotificationDto>` |
| GET | `/api/notifications/stream/user/{userId}` | **SSE Stream** | `Flux<NotificationDto>` |

## Database Management

### H2 Console
Access the in-memory database console at:
```
http://localhost:8082/h2-console
```
- **JDBC URL**: `r2dbc:h2:mem:///notificationdb` (or `jdbc:h2:mem:notificationdb`)
- **User**: `sa`
- **Password**: (empty)

---

## 🧪 Testing

### Run Unit Tests

```bash
mvn test
```

### Test SSE Stream (Terminal)

```bash
# Open SSE connection in one terminal
curl -N http://localhost:8082/api/notifications/stream/user/1
```

### Trigger Notification (Another Terminal)

```bash
# Create a booking (triggers notification)
curl -X POST http://localhost:8081/api/catalog/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "serviceId": "GYM",
    "serviceType": "AMENITY",
    "startTime": "2025-12-12T10:00:00",
    "endTime": "2025-12-12T11:00:00"
  }'
```

✅ You should see the notification appear in real-time in the SSE terminal!

### Test SSE in Browser

```javascript
const eventSource = new EventSource('http://localhost:8082/api/notifications/stream/user/1');
eventSource.onmessage = (event) => {
    console.log('Notification received:', JSON.parse(event.data));
};
```

---

## 🔬 Reactive vs Blocking Comparison

### Blocking Approach (Spring MVC + JPA)

```java
// BLOCKS thread waiting for database
@GetMapping("/notifications")
public List<Notification> getNotifications() {
    return repository.findAll();  // Thread blocked here
}
```

### Reactive Approach (WebFlux + R2DBC)

```java
// NON-BLOCKING, event-driven
@GetMapping("/notifications")
public Flux<Notification> getNotifications() {
    return repository.findAll();  // Returns immediately, data streamed later
}
```

**Benefits:**
- ✅ 10x higher concurrency (event loop vs threads)
- ✅ Better resource utilization
- ✅ Real-time streaming (SSE, WebSocket)
- ✅ Backpressure handling

---

## 📚 Learning Resources

### Code Examples in This Service

1. **Mono/Flux** → NotificationService.java
2. **R2DBC** → NotificationRepository.java
3. **Reactive Kafka** → BookingEventConsumer.java
4. **SSE** → NotificationController.java (streamNotifications)
5. **Hot Publisher** → NotificationStreamService.java
6. **WebClient** → PushNotificationService.java (commented example)
7. **Testing** → NotificationServiceTests.java (StepVerifier)

### Official Docs

- [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Project Reactor](https://projectreactor.io/docs/core/release/reference/)
- [R2DBC](https://r2dbc.io/)
- [Reactor Kafka](https://projectreactor.io/docs/kafka/release/reference/)

---

## 🔧 Configuration

### R2DBC (H2 In-Memory)

```yaml
spring:
  r2dbc:
    url: r2dbc:h2:mem:///notificationdb
```

For **PostgreSQL** (production):

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/notificationdb
    username: user
    password: pass
```

### Kafka Consumer

```yaml
kafka:
  bootstrap-servers: localhost:9093
  consumer:
    group-id: notification-service-group
  topics:
    booking-requests: booking-requests
```

---

## 🎓 What You Learned

After implementing this service, you now understand:

1. ✅ **Mono<T>** - Single async value (like CompletableFuture)
2. ✅ **Flux<T>** - Stream of async values (like Stream but async)
3. ✅ **flatMap()** - Chain async operations
4. ✅ **map()** - Transform data
5. ✅ **subscribe()** - Activate reactive pipeline
6. ✅ **R2DBC** - Reactive database queries
7. ✅ **Reactor Kafka** - Non-blocking event consumption
8. ✅ **SSE** - Server push to browser
9. ✅ **Hot vs Cold Publishers** - Shared vs independent streams
10. ✅ **StepVerifier** - Testing reactive code

---

## 🚧 Future Enhancements

- [ ] Add reactive Redis caching
- [ ] Implement real FCM/SendGrid integration
- [ ] Add WebSocket support
- [ ] Implement notification preferences
- [ ] Add notification templates
- [ ] Metrics with Micrometer
- [ ] Distributed tracing (Sleuth + Zipkin)

---

## 🐛 Troubleshooting

### Service Won't Start

```bash
# Check if port 8082 is available
lsof -i :8082

# Check Kafka is running
docker ps | grep kafka
```

### SSE Not Working

- Ensure browser supports EventSource API
- Check CORS settings for cross-origin requests
- Verify userId in stream matches booking userId

### Kafka Consumer Not Receiving

- Check topic exists: `docker exec -it <kafka-container> kafka-topics --list --bootstrap-server localhost:9092`
- Verify booking-service is producing to correct topic
- Check consumer group offset

---

## 📞 Support

For questions about reactive patterns or issues:
1. Check code comments (detailed explanations)
2. Review official Spring WebFlux documentation
3. Test with provided curl commands

---

**Built with ❤️ using Spring Boot 3.2.0 + Project Reactor**
