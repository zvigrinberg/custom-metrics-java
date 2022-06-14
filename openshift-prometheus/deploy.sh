#!/bin/bash
echo "Create new namespace..."
echo
oc new-project custom-metric-test
echo "Deploy pods on namespace..."
echo
oc apply -f pods.yaml
echo "create services for the pods .."
echo
oc apply -f services.yaml
echo "expose routes to services for the pods .."
echo
oc apply -f routes.yaml
echo "enable user workloads on openshift monitoring:"
echo
oc apply -f cluster-monitoring-config.yaml
oc apply -f user-workload-monitoring-config.yaml
echo "create Service Monitors to let Prometheus pull new application Metrics:"
echo
oc apply -f service-monitors.yaml


