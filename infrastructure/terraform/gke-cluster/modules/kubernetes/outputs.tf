output "cluster_host" {
  description = "The Kubernetes cluster endpoint"
  value       = google_container_cluster.primary.endpoint
}

output "cluster_ca" {
  description = "Cluster CA certificate"
  value       = base64decode(google_container_cluster.primary.master_auth[0].cluster_ca_certificate)
}

output "cluster_token" {
  description = "Access token for the Kubernetes cluster"
  value       = data.google_client_config.default.access_token
  sensitive   = true
}
