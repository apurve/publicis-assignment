#!/bin/bash

# Delete via Kustomize
echo "Tearing down via Kustomize..."
kubectl delete -k k8s/

# Cleanup Ingress Controller
echo "Cleaning up Ingress Controller..."
echo "Detected Docker Desktop. Removing NGINX Ingress Controller..."
kubectl delete -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml --ignore-not-found=true

echo "Teardown complete. All pods and services have been removed."
