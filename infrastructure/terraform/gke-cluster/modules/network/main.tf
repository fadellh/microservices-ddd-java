resource "google_compute_network" "vpc_network" {
  name                    = "mwc-vpc-network"
  auto_create_subnetworks = false
}

resource "google_compute_subnetwork" "subnet" {
  name          = "mwc-subnet"
  region        = var.region
  network       = google_compute_network.vpc_network.id
  ip_cidr_range = "10.0.0.0/24"
}

# output "network_id" {
#   value = google_compute_network.vpc_network.id
# }

# output "subnetwork_id" {
#   value = google_compute_subnetwork.subnet.id
# }
