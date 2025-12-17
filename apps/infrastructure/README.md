# Infrastructure

This directory contains infrastructure configuration for the apartment management system.

## Services

The infrastructure stack includes the following services managed via Docker Compose:

### 1. **Kafka**
- **Purpose**: Message broker for asynchronous communication between services
- **Port**: 9093 (external), 9092 (internal)
- **Topics**: `booking-requests` (auto-created)
- **Platform**: linux/arm64 (Apple Silicon optimized)

### 2. **Zookeeper**
- **Purpose**: Coordination service for Kafka
- **Port**: 2181
- **Platform**: linux/arm64 (Apple Silicon optimized)

### 3. **Redis**
- **Purpose**: In-memory cache for catalog service data
- **Port**: 6379
- **Persistence**: AOF (Append-Only File) enabled
- **Volume**: `redis-data` for data persistence
- **Platform**: linux/arm64 (Apple Silicon optimized)

## Prerequisites

- Docker Desktop installed and running
- **Docker Hub account** (required to pull images)

## Docker Hub Authentication

Before starting the infrastructure, authenticate with Docker Hub:
```bash
docker login
```
Enter your Docker Hub username and password when prompted.

## Starting Infrastructure

Start all services (Kafka, Zookeeper, Redis):
```bash
cd apps/infrastructure
docker-compose up -d
```

### Verify Services

Check that all containers are running:
```bash
docker-compose ps
```

Expected output:
```
NAME                       STATUS
infrastructure-kafka-1     Up
infrastructure-redis-1     Up
infrastructure-zookeeper-1 Up
```

### Test Individual Services

**Kafka:**
```bash
# List topics
docker exec infrastructure-kafka-1 kafka-topics --list --bootstrap-server localhost:9093
```

**Redis:**
```bash
# Ping Redis
docker exec infrastructure-redis-1 redis-cli ping
# Expected: PONG

# Check Redis info
docker exec infrastructure-redis-1 redis-cli INFO server
```

**Zookeeper:**
```bash
# Check Zookeeper status
docker exec infrastructure-zookeeper-1 /bin/bash -c "echo stat | nc localhost 2181"
```

## Stopping Infrastructure

Stop all services:
```bash
docker-compose down
```

Stop and remove volumes (⚠️ this will delete Redis data):
```bash
docker-compose down -v
```

## Monitoring

### View Logs

**All services:**
```bash
docker-compose logs -f
```

**Specific service:**
```bash
docker-compose logs -f kafka
docker-compose logs -f redis
docker-compose logs -f zookeeper
```

### Redis Monitoring

**Check memory usage:**
```bash
docker exec infrastructure-redis-1 redis-cli INFO memory
```

**Monitor commands in real-time:**
```bash
docker exec infrastructure-redis-1 redis-cli MONITOR
```

**View all keys:**
```bash
docker exec infrastructure-redis-1 redis-cli KEYS "*"
```

## Configuration

The `docker-compose.yml` file contains the following key configurations:

### Kafka Environment Variables
- `KAFKA_BROKER_ID`: 1
- `KAFKA_ZOOKEEPER_CONNECT`: zookeeper:2181
- `KAFKA_ADVERTISED_LISTENERS`: PLAINTEXT://localhost:9093
- `KAFKA_AUTO_CREATE_TOPICS_ENABLE`: "true"
- `KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR`: 1

### Redis Configuration
- **Command**: `redis-server --appendonly yes`
- **Volume Mount**: `redis-data:/data`

## Troubleshooting

### Kafka Connection Issues
If services can't connect to Kafka:
1. Verify Kafka is running: `docker ps | grep kafka`
2. Check Kafka logs: `docker-compose logs kafka`
3. Ensure port 9093 is not in use: `lsof -i :9093`

### Redis Connection Issues
If catalog service can't connect to Redis:
1. Verify Redis is running: `docker ps | grep redis`
2. Test connection: `docker exec infrastructure-redis-1 redis-cli ping`
3. Check Redis logs: `docker-compose logs redis`

### Port Conflicts
If ports are already in use:
- Kafka (9093): Change in `docker-compose.yml` and update service configurations
- Redis (6379): Change in `docker-compose.yml` and update `catalog-service/application.yml`
- Zookeeper (2181): Change in `docker-compose.yml` and update Kafka configuration

### Reset Everything
To completely reset the infrastructure:
```bash
docker-compose down -v
docker-compose up -d
```

## Manual Docker Installation

If Docker installation fails via Homebrew, download Docker Desktop manually from:
https://www.docker.com/products/docker-desktop

After installation:
1. Open Docker Desktop application
2. Wait for Docker daemon to start
3. Run `docker-compose up -d` in this directory

## Performance Tips

### Redis
- Monitor memory usage regularly
- Consider setting maxmemory policy for production
- Use Redis persistence (AOF) for important data

### Kafka
- Monitor broker metrics
- Adjust retention policies based on volume
- Consider replication factor for production

## Data Persistence

- **Redis**: Data persists in the `redis-data` Docker volume using AOF
- **Kafka**: Messages persist according to retention policies
- **Zookeeper**: Metadata persists in container

To backup Redis data:
```bash
docker exec infrastructure-redis-1 redis-cli SAVE
docker cp infrastructure-redis-1:/data/dump.rdb ./backup/
```
