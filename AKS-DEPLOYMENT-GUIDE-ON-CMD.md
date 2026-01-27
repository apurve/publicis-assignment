# Azure Kubernetes Service (AKS) Deployment Guide

Complete step-by-step guide to deploy the Booking, Catalog, and Notification services to Azure Kubernetes Service.

## Prerequisites

### Required Tools

1. **Azure CLI** (version 2.30.0 or later)
   ```bash
   # Check version
   az --version
   ```

2. **kubectl** (Kubernetes CLI)
   ```bash
   # Check version
   kubectl version --client
   ```

3. **Docker** (for building images)
   ```bash
   # Check version
   docker --version
   ```

4. **Java 8+ and Maven** (for building applications)
   ```bash
   # Check versions
   java -version
   mvn -version
   ```

### Azure Resources Required

- Azure Subscription
- Resource Group
- Azure Container Registry (ACR)
- Azure Kubernetes Service (AKS) cluster

---

## Step 1: Azure Setup

### 1.1 Login to Azure

```bash
# Login to Azure
az login

# Set your subscription (if you have multiple)
az account list --output table
az account set --subscription "<your-subscription-id>"

# Verify current subscription
az account show
```

### 1.2 Create Resource Group (if not exists)

```bash
# Set variables
RESOURCE_GROUP="rg-apugupta-aks"
LOCATION="eastus2" # Using eastus2 for better SKU availability

# Create resource group
az group create --name $RESOURCE_GROUP --location $LOCATION
```

### 1.3 Create Azure Container Registry (ACR)

```bash
# Set ACR name (must be globally unique, lowercase, alphanumeric only)
ACR_NAME="acrapugupta"

# Create ACR
az acr create --resource-group $RESOURCE_GROUP --name $ACR_NAME --sku Basic

# Enable admin user (for simple authentication)
az acr update --name $ACR_NAME --admin-enabled true

# Get ACR login server
az acr show --name $ACR_NAME --query loginServer --output tsv
# ACR_LOGIN_SERVER = acrapugupta.azurecr.io
```

### 1.4 Create AKS Cluster (if not exists)

```bash
# Set AKS cluster name
AKS_CLUSTER="aks-apugupta"

# Create AKS cluster (this takes several minutes)
az aks create \
  --resource-group $RESOURCE_GROUP \
  --name $AKS_CLUSTER \
  --node-count 1 \
  --node-vm-size standard_d2s_v3 \
  --generate-ssh-keys \
  --attach-acr $ACR_NAME \
  --enable-managed-identity

# Get AKS credentials
az aks get-credentials --resource-group $RESOURCE_GROUP --name $AKS_CLUSTER --overwrite-existing

# Verify connection
kubectl get nodes
```

DONE

**Expected Output:**
```
NAME                                STATUS   ROLES   AGE   VERSION
aks-nodepool1-12345678-vmss000000   Ready    agent   5m    v1.27.7
aks-nodepool1-12345678-vmss000001   Ready    agent   5m    v1.27.7
```

---

## Step 2: Build Applications

### 2.1 Navigate to Project Root

```bash
cd /Users/apurvegupta/Documents/public-repo/apurve-gupta/apps
```

### 2.2 Build Microservices
Build all services using Maven:

```bash
# Build Booking Service
cd booking-service && mvn clean package -DskipTests

# Build Catalog Service
cd ../catalog-service && mvn clean package -DskipTests

# Build Notification Service
cd ../notification-service && mvn clean package -DskipTests
```

---

## Step 3: Create and Push Docker Images

### 3.1 Login to Container Registry

```bash
az acr login --name $ACR_NAME
```

### 3.2 Build and Push Images

```bash
# Variables
ACR_LOGIN_SERVER=$(az acr show --name $ACR_NAME --query loginServer --output tsv)

# Booking Service
cd apps/booking-service
docker build --platform linux/amd64 -t booking-service:latest .
docker tag booking-service:latest $ACR_LOGIN_SERVER/booking-service:latest
docker push $ACR_LOGIN_SERVER/booking-service:latest

# Catalog Service
cd ../catalog-service
docker build --platform linux/amd64 -t catalog-service:latest .
docker tag catalog-service:latest $ACR_LOGIN_SERVER/catalog-service:latest
docker push $ACR_LOGIN_SERVER/catalog-service:latest

# Notification Service
cd ../notification-service
docker build --platform linux/amd64 -t notification-service:latest .
docker tag notification-service:latest $ACR_LOGIN_SERVER/notification-service:latest
docker push $ACR_LOGIN_SERVER/notification-service:latest
```

**Expected Output:**
```
Result
--------
1.0.0
latest
```

---

## Step 4: Prepare Kubernetes Manifests

The manifests are located in `k8s/apps`. We use **Kustomize** to manage the deployment.

### 4.1 Update Image References (Kustomize)

The `deploy_aks.sh` script handles image tagging automatically, but if you want to do it manually:

```bash
cd k8s/apps

# Edit kustomization.yaml or use kustomize edit
kustomize edit set image booking-service=$ACR_LOGIN_SERVER/booking-service:latest
kustomize edit set image catalog-service=$ACR_LOGIN_SERVER/catalog-service:latest
kustomize edit set image notification-service=$ACR_LOGIN_SERVER/notification-service:latest
```

---

## Step 5: Deploy to AKS

### 5.1 Apply Infrastructure
First, deploy the supporting services (Kafka, Redis, Zookeeper):

```bash
kubectl apply -k k8s/infrastructure
```

### 5.2 Deploy Applications
Apply all microservices and their configurations:

```bash
kubectl apply -k k8s/apps
```

---

### 5.3 Verify Deployment

```bash
# Check both services are running
kubectl get pods
kubectl get svc
kubectl get deployments
```

---

## Step 6: Expose Services

### 6.1 Get External IPs
The services are exposed via dedicated Azure Load Balancers defined in `load-balancers.yaml`.

```bash
# Get the external IPs
kubectl get svc
```

Look for:
- `booking-service-external`
- `catalog-service-external`
- `notification-service-external`

### 6.2 Test Access
Access the services using their external IPs:
```bash
# Catalog Service
curl http://<CATALOG_IP>/api/catalog/services

# Booking Service
curl http://<BOOKING_IP>/api/bookings

# Notification Service (SSE)
curl -N http://<NOTIFICATION_IP>/api/notifications/stream/user/1
```

---

## Step 7: Monitoring and Troubleshooting

### 7.1 View Logs

```bash
# Real-time logs
kubectl logs -f -l app=catalog-service
kubectl logs -f -l app=booking-service
kubectl logs -f -l app=notification-service
```

### 7.2 Check Resource Usage

```bash
# CPU and memory usage
kubectl top pods
kubectl top nodes
```

### 7.3 Common Issues and Solutions

**Issue: ImagePullBackOff**
```bash
# Verify ACR integration
az aks check-acr \
  --resource-group $RESOURCE_GROUP \
  --name $AKS_CLUSTER \
  --acr $ACR_NAME

# Reattach ACR if needed
az aks update \
  --resource-group $RESOURCE_GROUP \
  --name $AKS_CLUSTER \
  --attach-acr $ACR_NAME
```

---

## Quick Reference Commands

### Build and Push
```bash
# From root
ACR_LOGIN_SERVER=$(az acr show --name $ACR_NAME --query loginServer --output tsv)

cd apps/booking-service && mvn clean package -DskipTests && docker build -t $ACR_LOGIN_SERVER/booking-service:latest . && docker push $ACR_LOGIN_SERVER/booking-service:latest
cd ../catalog-service && mvn clean package -DskipTests && docker build -t $ACR_LOGIN_SERVER/catalog-service:latest . && docker push $ACR_LOGIN_SERVER/catalog-service:latest
cd ../notification-service && mvn clean package -DskipTests && docker build -t $ACR_LOGIN_SERVER/notification-service:latest . && docker push $ACR_LOGIN_SERVER/notification-service:latest
```

### Deploy
```bash
kubectl apply -k k8s/infrastructure
kubectl apply -k k8s/apps
```

### Cleanup
```bash
# Delete Azure resources (WARNING: Permanent)
az group delete --name $RESOURCE_GROUP --yes --no-wait
```

---

## Summary

✅ **Prerequisites**: Azure CLI, kubectl, Docker, Java/Maven  
✅ **Azure Setup**: Resource Group, ACR, AKS cluster (`standard_d2s_v3`)  
✅ **Build**: Maven packages for all microservices  
✅ **Docker**: Images built and pushed to ACR  
✅ **Deploy**: Infrastructure and Apps deployed via Kustomize  
✅ **Verify**: External access via dedicated Azure Load Balancers  

Your microservices are now running in Azure Kubernetes Service! 🎉
