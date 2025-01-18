###############################################################################
# OUTPUTS
###############################################################################
output "kubernetes_cluster_endpoint" {
  description = "The Kubernetes cluster endpoint"
  value       = module.kubernetes.cluster_host
}

# output "api_gateway_url" {
#   description = "The API Gateway URL"
#   value       = module.api_gateway.gateway_hostname
# }

output "cluster_token" {
  description = "Access token for the Kubernetes cluster"
  value       = data.google_client_config.default.access_token
  sensitive   = true
}
