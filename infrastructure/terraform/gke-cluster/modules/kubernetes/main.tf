resource "google_container_cluster" "primary" {
  name                = "primary-mwc-cluster"
  location            = var.region_zone
  network             = var.network_id
  subnetwork          = var.subnetwork_id
  deletion_protection = false

  node_pool {
    name = "mwc-node-pool"

    node_config {
      machine_type = "custom-2-10240"
      disk_size_gb = 100
      disk_type    = "pd-standard"
    }
    initial_node_count = 1

    autoscaling {
      min_node_count = 1
      max_node_count = 2
    }

  }
}

data "google_client_config" "default" {}



# output "cluster_host" {
#   value = google_container_cluster.primary.endpoint
# }

# output "cluster_ca" {
#   value = base64decode(google_container_cluster.primary.master_auth[0].cluster_ca_certificate)
# }

# output "cluster_token" {
#   value = data.google_client_config.default.access_token
# }
