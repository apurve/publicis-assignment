#!/bin/bash
set -e

# Configuration
TERRAFORM_DIR="./terraform"
K8S_APPS_DIR="./k8s/apps"
K8S_INFRA_DIR="./k8s/infrastructure"

echo "🚀 Starting Automated AKS Deployment..."

# 2. Get Infrastructure Details
echo "📝 Getting infrastructure details..."
RESOURCE_GROUP=$(terraform output -raw resource_group_name)
AKS_CLUSTER=$(terraform output -raw aks_cluster_name)
ACR_LOGIN_SERVER=$(terraform output -raw acr_login_server)
ACR_NAME=$(echo $ACR_LOGIN_SERVER | cut -d'.' -f1)

# 3. Configure Kubectl
echo "☸️  Configuring kubectl context..."
az aks get-credentials --resource-group $RESOURCE_GROUP --name $AKS_CLUSTER --overwrite-existing

# 4. Login to ACR
echo "🔐 Logging into ACR..."
az acr login --name $ACR_NAME

# 5. Building and Pushing Docker Images
echo "📦 Building and pushing microservices..."
cd ..

# Services list
SERVICES=("booking-service" "catalog-service" "notification-service")

for SERVICE in "${SERVICES[@]}"; do
    echo "🔨 Building $SERVICE..."
    docker build --platform linux/amd64 -t $SERVICE:latest apps/$SERVICE
    
    echo "🏷️  Tagging $SERVICE..."
    docker tag $SERVICE:latest $ACR_LOGIN_SERVER/$SERVICE:latest
    
    echo "📤 Pushing $SERVICE to ACR..."
    docker push $ACR_LOGIN_SERVER/$SERVICE:latest
done

# 5. Deploy to Kubernetes
echo "🚀 Deploying to Kubernetes cluster..."

# Use Kustomize to update images and apply
cd $K8S_APPS_DIR
for SERVICE in "${SERVICES[@]}"; do
    kustomize edit set image $SERVICE=$ACR_LOGIN_SERVER/$SERVICE:latest
done

cd ../..
echo "🧱 Applying Infrastructure manifests..."
kubectl apply -k $K8S_INFRA_DIR

echo "🧩 Applying Application manifests..."
kubectl apply -k $K8S_APPS_DIR

echo "✅ Deployment Successful!"
echo "------------------------------------------------"
echo "Check your services with: kubectl get svc"
echo "------------------------------------------------"
