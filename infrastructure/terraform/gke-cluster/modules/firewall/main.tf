###############################################################################
# FIREWALL FOR GKE ACCESS
###############################################################################
resource "google_compute_firewall" "gke_firewall" {
  name    = "gke-firewall"
  network = var.network_id

  allow {
    protocol = "tcp"
    ports    = ["80", "443", "10250"]
  }

  source_ranges = ["0.0.0.0/0"]
}

# output "firewall_name" {
#   value = google_compute_firewall.gke_firewall.name
# }
