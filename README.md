# Microservices Applications

This directory contains the microservices for the system, fully migrated to a **Kubernetes-native** architecture.

## Modules

### 1. [Booking Service](./apps/booking-service)
A booking and reservation management service.
- **Port**: 8080
- **Database**: H2 (In-memory)
- **Features**: REST API, Business Logic, OpenAPI/Swagger.
- **Discovery**: Kubernetes DNS.
- **Config**: Kubernetes ConfigMap (`booking-config`).

### 2. [Catalog Service](./apps/catalog-service)
A service catalog management system.
- **Port**: 8081
- **Cache**: Redis.
- **Features**: Service browsing, Async booking via Kafka.
- **Discovery**: Kubernetes DNS.
- **Config**: Kubernetes ConfigMap (`catalog-config`).

### 3. [Notification Service](./apps/notification-service) ⚡ **Reactive**
A reactive notification service.
- **Port**: 8082
- **Stack**: Spring WebFlux, R2DBC (H2), Reactor Kafka.
- **Features**: Real-time SSE notifications.
- **Config**: Kubernetes ConfigMap (`notification-config`).

### 4. [Infrastructure](./k8s/infrastructure)
Kubernetes manifests for the supporting services:
- **Kafka** (9092)
- **Zookeeper** (2181)
- **Redis** (6379)

## Getting Started

### Prerequisites

- **Docker Desktop** (with Kubernetes enabled) OR **Minikube**
- **Java 17**
- **Maven**
- **kubectl**

### Quick Start

1.  **Start Kubernetes**: Ensure your cluster is running (e.g. Docker Desktop status is green).

2.  **Deploy System**:
    Run the helper script to build images and apply Kubernetes manifests:
    ```bash
    ./deploy_k8s.sh
    ```

3.  **Verify Deployment**:
    ```bash
    kubectl get pods
    ```
    Wait until all pods (`booking-service`, `catalog-service`, `notification-service`, `kafka`, `redis`, `zookeeper`) are `Running`.

### Deploy to Azure Kubernetes Service (AKS)

To deploy this application to Azure AKS, use the provided automation script.

**Prerequisites**:
- **Azure CLI** (`az`): Install via `brew install azure-cli`
- **Docker**
- **kubectl**
- An **Azure account** with an active subscription

**Steps**:
1.  **Run the deployment script**:
    ```bash
    ./deploy_aks.sh
    ```
2.  **Follow the interactive Azure login** prompt in your browser.
3.  The script will automatically:
    - Create a Resource Group (`rg-apurve-gupta-aks`)
    - Create an Azure Container Registry (ACR)
    - Create an AKS Cluster and attach the ACR
    - Build and push Docker images to ACR
    - Deploy manifests to AKS using Kustomize
4.  **Verify**:
    ```bash
    kubectl get pods
    kubectl get svc
    ```

> **Note**: The script modifies `k8s/kustomization.yaml` temporarily to set ACR image paths and then restores it.

### Accessing Services

#### Via Ingress (Recommended)

An **NGINX Ingress Controller** is deployed with the application. Get the external IP:
```bash
kubectl get svc -n ingress-nginx ingress-nginx-controller
```

Once you have the `EXTERNAL-IP`, access services at:
| Service | URL |
|---------|-----|
| **Catalog API** | `http://<EXTERNAL-IP>/api/catalog` |
| **Bookings API** | `http://<EXTERNAL-IP>/api/bookings` |
| **Notifications API** | `http://<EXTERNAL-IP>/api/notifications` |

> **Note**: On local clusters (Docker Desktop/Minikube), the external IP may show as `localhost` or `<pending>`. For Minikube, run `minikube tunnel`.

#### Via Port Forwarding (Alternative)

For direct access or debugging, use `kubectl port-forward`:

```bash
# Catalog Service
kubectl port-forward svc/catalog-service 8081:8081
# URL: http://localhost:8081/swagger-ui/index.html

# Booking Service
kubectl port-forward svc/booking-service 8080:8080
# URL: http://localhost:8080/swagger-ui/index.html

# Notification Service
kubectl port-forward svc/notification-service 8082:8082
# URL: http://localhost:8082/swagger-ui/index.html
```

## Architecture Changes

This project has been refactored from a Spring Cloud ecosystem to **Cloud Native Kubernetes**:

| Feature | Old Approach | New Kubernetes Approach |
|---------|--------------|-------------------------|
| **Service Discovery** | Netflix Eureka | Kubernetes Service DNS (`http://service-name`) |
| **Configuration** | Spring Cloud Config Server | Kubernetes ConfigMaps |
| **Gateway/Routing** | (Direct/Zuul) | **NGINX Ingress Controller** |
| **Orchestration** | Docker Compose | Kubernetes Deployments |

## Infrastructure

The **active** infrastructure definition is in `k8s/infrastructure/`.
