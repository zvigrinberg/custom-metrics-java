#!/bin/bash
set -x
echo "Create new namespace..."
echo
oc new-project custom-metric-test
echo "Deploy pods on namespace..."
echo
oc apply -f pods.yaml
echo "Create services for the pods .."
echo
oc apply -f services.yaml
echo "Expose routes to services for the pods .."
echo
oc apply -f routes.yaml
echo "Enabling user workloads on openshift monitoring:"
echo
oc apply -f cluster-monitoring-config.yaml
oc apply -f user-workload-monitoring-config.yaml
echo "Create Service Monitors to let Prometheus pull new application Metrics:"
echo
oc apply -f service-monitors.yaml
echo "Creating a datasource in grafana using the grafana operator and a grafanaDataSource CR instance:"
echo
SECRET=$(oc get secret -n openshift-user-workload-monitoring | grep  prometheus-user-workload-token | head -n 1 | awk '{print $1 }')
TOKEN=`echo $(oc get secret $SECRET -n openshift-user-workload-monitoring -o json | jq -r '.data.token') | base64 -d`
sed 's/TOKEN_PLACEHOLDER/'""$TOKEN""'/g' grafana-data-source.yaml | oc apply -f - -n custom-metric-test

echo "Creating a dashboard for application metrics"
oc apply -f custom-java-metrics-dashboard.yaml
echo "Done!"
echo
exit 0





