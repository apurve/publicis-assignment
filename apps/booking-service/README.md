# Booking Service

A microservice for managing apartment amenity bookings with comprehensive OpenAPI documentation.

## Features

- **REST API** for creating bookings
- **Business Logic Validation**:
  - User maintenance fee verification
  - Amenity availability checking
- **H2 In-Memory Database** for prototype
- **Service Discovery** integration with Eureka
- **Configuration Management** via Config Server
- **OpenAPI 3.0 Documentation** with Swagger UI

## API Documentation

### Swagger UI
Access interactive API documentation at:
```
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI Specification
Download the complete OpenAPI spec at:
```
http://localhost:8080/v3/api-docs
```

## Database Management

### H2 Console
Access the in-memory database console at:
```
http://localhost:8080/h2-console
```
- **JDBC URL**: `jdbc:h2:mem:bookingdb`
- **User**: `sa`
- **Password**: (empty)

## API Endpoints

### POST /api/bookings
Creates a new booking for an amenity.

**Request Body:**
```json
{
  "userId": 1,
  "amenityId": "GYM",
  "startTime": "2025-12-01T10:00:00",
  "endTime": "2025-12-01T11:00:00"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "amenityId": "GYM",
  "startTime": "2025-12-01T10:00:00",
  "endTime": "2025-12-01T11:00:00",
  "status": "CONFIRMED"
}
```

**Error Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Maintenance fee not paid for user: 1",
  "timestamp": 1701360000000
}
```

## Running the Service

```bash
mvn spring-boot:run
```

The service will start on port 8080 and register with Eureka.

## Architecture

### Domain Layer
- `Booking`: Entity representing a reservation
- `User`: Model for resident information
- `Amenity`: Model for facility information

### Persistence Layer
- `BookingRepository`: JPA repository for database operations

### Business Logic Layer
- `BookingService`: Validates business rules and manages booking creation

### Presentation Layer
- `BookingController`: REST endpoints with OpenAPI annotations
- `BookingRequest/Response`: DTOs with schema documentation
- `ErrorResponse`: Standard error format

## Configuration

The service fetches configuration from Config Server at startup. All configuration (including Database and Kafka settings) is managed centrally in the Config Server.

## Dependencies

- Spring Boot 3.2.0
- Spring Cloud Config Client
- Spring Cloud Netflix Eureka Client
- Spring Data JPA
- H2 Database
- **SpringDoc OpenAPI 2.3.0**
