# Build Docker images
echo "Building Docker images..."
docker build -t booking-service:latest apps/booking-service
docker build -t catalog-service:latest apps/catalog-service
docker build -t notification-service:latest apps/notification-service

# Apply Kustomize Configuration
echo "Deploying via Kustomize..."
kubectl apply -k k8s/

echo "Waiting for pods to be ready..."
kubectl wait --for=condition=ready pod -l app=catalog-service --timeout=60s

echo "Deployment complete."
