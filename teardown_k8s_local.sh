#!/bin/bash

# Delete via Kustomize
echo "Tearing down via Kustomize..."
kubectl delete -k k8s/


echo "Teardown complete. All pods and services have been removed."
