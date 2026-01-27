# 🏥 Apartment Management Microservices

A robust, cloud-native microservices ecosystem migrated from a traditional Spring Cloud architecture to a **Kubernetes-native** environment. This project demonstrates high-availability deployment patterns, reactive programming, and Infrastructure as Code (IaC) using Terraform.

---

## 🏗️ Architecture Overview

The system is composed of several specialized microservices communicating via Kubernetes DNS and event streams (Kafka).

| Service | Responsibility | Port | Tech Stack |
|:---|:---|:---:|:---|
| **[Booking](./apps/booking-service)** | Amenity & reservation management | 8080 | Java, Spring Boot, H2 |
| **[Catalog](./apps/catalog-service)** | Service discovery and cache management | 8081 | Java, Spring Boot, Redis, Kafka |
| **[Notification](./apps/notification-service)** | Real-time reactive updates | 8082 | Spring WebFlux, R2DBC, Kafka |

### 🛠️ Key Infrastructure
- **Message Broker**: Apache Kafka (9092)
- **State Store**: Redis (6379)
- **Persistence**: H2 (In-memory/R2DBC)
- **External Access**: Azure Load Balancer

---

## 🚀 Deployment Guide

### 1. Azure AKS Deployment (Terraform - Recommended)
The project includes a full Terraform suite to provision your professional environment on Azure in minutes.

**Location**: `./terraform`

```bash
# Workflow
cd terraform
terraform init
terraform apply -var="acr_name=acrapugupta" # Overrides if needed
```

**Provisioned Resources**:
- **Resource Group**: `rg-apugupta-aks`
- **ACR**: `acrapugupta` (Alphanumeric registry)
- **AKS**: `aks-apugupta` (Standard Load Balancer enabled)

### 2. Local Kubernetes (Docker Desktop / Kind / Minikube)
For rapid local development, use the provided helper scripts:
```bash
# Build images and deploy manifests
./deploy_k8s_local.sh

# Cleanup environment
./teardown_k8s_local.sh
```

---

## 🌐 Accessing the Services

### ☁️ Cloud Access (Azure)
Each service is provisioned with a dedicated **Azure Load Balancer**.

```bash
# Retrieve public IPs
kubectl get svc -l access=external
```

| Service | Endpoint |
|:---|:---|
| **Catalog API** | `http://<CATALOG_LB_IP>/api/catalog` |
| **Booking API** | `http://<BOOKING_LB_IP>/api/bookings` |
| **Notification API** | `http://<NOTIFICATION_LB_IP>/api/notifications` |

### 💻 Local Debugging
Use port-forwarding to access services directly from your workstation:
```bash
# Catalog: http://localhost:8081/swagger-ui/index.html
kubectl port-forward svc/catalog-service 8081:8081

# Booking: http://localhost:8080/swagger-ui/index.html
kubectl port-forward svc/booking-service 8080:8080
```

---

## 🔄 Cloud Native Refactoring
This project was successfully migrated from a legacy stack to a modern Kubernetes architecture.

| Feature | Legacy Approach | **Cloud-Native Approach** |
|:---|:---|:---|
| **Service Discovery** | Netflix Eureka | Kubernetes Service DNS |
| **Configuration** | Spring Cloud Config | Kubernetes ConfigMaps |
| **Load Balancing** | Zuul / Ribbon | **Azure Load Balancer** |
| **Provisioning** | Manual Scripts | **Terraform (IaC)** |
| **Deployment** | Docker Compose | K8s Deployments & Kustomize |
