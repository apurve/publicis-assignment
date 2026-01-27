#!/bin/bash
set -e

# Configuration
TERRAFORM_DIR="./terraform"
K8S_APPS_DIR="./k8s/apps"
K8S_INFRA_DIR="./k8s/infrastructure"

echo "🗑️ Starting Automated AKS Teardown..."

# 1. Kubernetes Cleanup (Optional but good practice)
echo "🧩 Cleaning up Kubernetes resources..."
kubectl delete -k $K8S_APPS_DIR --ignore-not-found
kubectl delete -k $K8S_INFRA_DIR --ignore-not-found

# 2. Terraform Destroy
echo "🏗️  Destroying Infrastructure via Terraform..."
cd $TERRAFORM_DIR
terraform destroy -auto-approve

echo "✅ Teardown Complete!"
