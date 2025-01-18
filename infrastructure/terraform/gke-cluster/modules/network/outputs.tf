output "network_id" {
  description = "The ID of the VPC network"
  value       = google_compute_network.vpc_network.id
}

output "subnetwork_id" {
  description = "The ID of the subnet"
  value       = google_compute_subnetwork.subnet.id
}
