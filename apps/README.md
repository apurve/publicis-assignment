# Verification Guide (Kubernetes)

This document outlines the steps to verify the functionality of the Microservices Booking System running on **Kubernetes**.

## 1. Infrastructure Status

Ensure all pods are running:

```bash
kubectl get pods
```

Expected Output:
- `kafka` (Running)
- `zookeeper` (Running)
- `redis` (Running)
- `booking-service` (Running)
- `catalog-service` (Running)
- `notification-service` (Running)

## 2. Accessing Services

### Option 1: Azure Load Balancer (Cloud Deployment)
Access services via their respective external IPs:

- **Catalog API**: `http://<CATALOG_LB_IP>/api/catalog/services`
- **Booking API**: `http://<BOOKING_LB_IP>/api/bookings`
- **Notification Stream**: `http://<NOTIFICATION_LB_IP>/api/notifications/stream/user/1`

> [!TIP]
> Run `kubectl get svc` to find the external IPs of the `-external` services.

### Option 2: Port Forwarding (Local/Debug)
You can also forward ports to access services individually:

```bash
# Terminal 1 - Catalog Service
kubectl port-forward svc/catalog-service 8081:8081

# Terminal 2 - Booking Service
kubectl port-forward svc/booking-service 8080:8080

# Terminal 3 - Notification Service
kubectl port-forward svc/notification-service 8082:8082
```

## 3. Redis Caching Verification (Catalog Service)

### Step 3.1: Trigger Cache Miss
```bash
curl http://localhost/api/catalog/services
```
*Expected Result*: Returns list of services. Latency: Higher.

### Step 3.2: Trigger Cache Hit
```bash
curl http://localhost/api/catalog/services
```
*Expected Result*: Returns list of services. Latency: Lower (served from Redis).

### Step 3.3: Inspect Redis
```bash
# Get Redis Pod Name
REDIS_POD=$(kubectl get pod -l app=redis -o jsonpath="{.items[0].metadata.name}")

# Check Cache Key
kubectl exec $REDIS_POD -- redis-cli GET catalog:services
```

## 4. End-to-End Verification (Booking Flow)

### Step 4.1: Subscribe to Notifications (SSE)
Open a terminal to listen for real-time updates:
```bash
curl -N http://localhost/api/notifications/stream/user/1
```

### Step 4.2: Trigger Booking Request
In a separate terminal, submit a booking:
```bash
curl -X POST http://localhost/api/catalog/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "serviceId": "GYM",
    "serviceType": "AMENITY",
    "startTime": "2025-12-02T10:00:00",
    "endTime": "2025-12-02T11:00:00"
  }'
```

### Step 4.3: Verify Results
1.  **Response**: You should receive a `202 Accepted` status with `{"status": "PENDING"}`.
2.  **Notification**: The SSE terminal window should verify a new notification event.
3.  **Logs**:
    Check Booking Service logs to confirm processing:
    ```bash
    kubectl logs -l app=booking-service --tail=20
    ```

## 5. Troubleshooting

### Connection Refused
If `curl` fails, ensure your `kubectl port-forward` commands are still running and haven't timed out.

### Kafka Issues
If bookings are stuck in "PENDING" (no notification):
1. check Kafka logs:
   ```bash
   kubectl logs -l app=kafka
   ```
2. Verify Notification Service logs:
   ```bash
   kubectl logs -l app=notification-service
   ```

### Reset System
To restart all application pods:
```bash
kubectl delete pod -l app=booking-service
kubectl delete pod -l app=catalog-service
kubectl delete pod -l app=notification-service
```
Kubernetes will automatically recreate them.
