# Kubernetes Manifests

This directory contains the Kubernetes resource definitions for the application.

## Structure

### [apps/](./apps)
Contains the application workloads and configuration.

- **Deployments & Services**:
  - `booking-service.yaml`
  - `catalog-service.yaml`
  - `notification-service.yaml`

- **Configuration (ConfigMaps)**:
  - `configmap-booking.yaml`
  - `configmap-catalog.yaml`
  - `configmap-notification.yaml`

### [infrastructure/](./infrastructure)
Contains the supporting infrastructure services.

- `kafka.yaml` (Deployment + Service)
- `zookeeper.yaml` (Deployment + Service)
- `redis.yaml` (Deployment + Service)

## Usage

### Apply All Resources
You can apply all resources using the deployment script in the root directory:
```bash
./deploy_k8s.sh
```

### Apply Manually
```bash
# Apply Infrastructure first
kubectl apply -f infrastructure/

# Then apply Apps
kubectl apply -f apps/
```
