#!/bin/bash
set -e

# Configuration Variables
RESOURCE_GROUP="rg-apurve-gupta-aks"
LOCATION="eastus"
ACR_NAME="acrapurvegupta" # Must be globally unique
AKS_CLUSTER_NAME="aks-apurve-gupta"
AKS_NODE_COUNT=2

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}Starting Deployment to Azure Kubernetes Service (AKS)...${NC}"

# 1. Prerequisites Check
echo -e "${YELLOW}Checking prerequisites...${NC}"

if ! command -v az &> /dev/null; then
    echo -e "${RED}Error: Azure CLI (az) is not installed. Please install it to proceed.${NC}"
    echo "Install via Homebrew: brew update && brew install azure-cli"
    exit 1
fi

if ! command -v kubectl &> /dev/null; then
    echo -e "${RED}Error: kubectl is not installed.${NC}"
    exit 1
fi

if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: docker is not installed.${NC}"
    exit 1
fi

# 2. Azure Login
echo -e "${YELLOW}Please login to Azure...${NC}"
az login --output none

# 3. Create Resource Group
echo -e "${YELLOW}Creating/Updating Resource Group: $RESOURCE_GROUP...${NC}"
az group create --name $RESOURCE_GROUP --location $LOCATION

# 4. Create Azure Container Registry (ACR)
echo -e "${YELLOW}Creating/Updating ACR: $ACR_NAME...${NC}"
az acr create --resource-group $RESOURCE_GROUP --name $ACR_NAME --sku Basic

# Login to ACR
echo -e "${YELLOW}Logging into ACR...${NC}"
az acr login --name $ACR_NAME

# Get ACR Login Server
ACR_LOGIN_SERVER=$(az acr show --name $ACR_NAME --query loginServer --output tsv)
echo -e "${GREEN}ACR Login Server: $ACR_LOGIN_SERVER${NC}"

# 5. Create AKS Cluster
echo -e "${YELLOW}Creating/Updating AKS Cluster: $AKS_CLUSTER_NAME... (This may take a while)${NC}"
az aks create \
    --resource-group $RESOURCE_GROUP \
    --name $AKS_CLUSTER_NAME \
    --node-count $AKS_NODE_COUNT \
    --generate-ssh-keys \
    --attach-acr $ACR_NAME

# Get credentials for kubectl
echo -e "${YELLOW}Getting AKS credentials...${NC}"
az aks get-credentials --resource-group $RESOURCE_GROUP --name $AKS_CLUSTER_NAME --overwrite-existing

# 6. Build and Push Images
echo -e "${YELLOW}Building and Pushing Docker images...${NC}"

APPS=("booking-service" "catalog-service" "notification-service")

for APP in "${APPS[@]}"; do
    echo -e "Processing $APP..."
    # Build
    docker build -t $ACR_LOGIN_SERVER/$APP:latest apps/$APP
    # Push
    docker push $ACR_LOGIN_SERVER/$APP:latest
done

# 7. Deploy to AKS
echo -e "${YELLOW}Deploying manifests to AKS...${NC}"
cd k8s

# Back up kustomization.yaml
cp kustomization.yaml kustomization.yaml.bak

# Append image replacements to kustomization.yaml (avoids dependency on 'kustomize edit')
cat <<EOF >> kustomization.yaml

images:
  - name: booking-service:latest
    newName: $ACR_LOGIN_SERVER/booking-service
    newTag: latest
  - name: catalog-service:latest
    newName: $ACR_LOGIN_SERVER/catalog-service
    newTag: latest
  - name: notification-service:latest
    newName: $ACR_LOGIN_SERVER/notification-service
    newTag: latest
EOF

# Check for Ingress Controller
echo -e "${YELLOW}Applying NGINX Ingress Controller...${NC}"
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml

echo -e "${YELLOW}Applying Application Manifests...${NC}"
kubectl apply -k .

# Restore kustomization.yaml to keep git clean
mv kustomization.yaml.bak kustomization.yaml

cd ..

# 8. Verification
echo -e "${YELLOW}Waiting for pods to be ready...${NC}"
kubectl wait --for=condition=ready pod -l app=catalog-service --timeout=120s

echo -e "${GREEN}Deployment Complete!${NC}"
echo -e "You can check services using: kubectl get svc"
