terraform {
  required_providers {
    cloudflare = {
      source  = "cloudflare/cloudflare"
      version = "~> 4.0"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "2.17.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.0"
    }
    google-beta = {
      source  = "hashicorp/google-beta"
      version = "~> 4.0"
    }
  }
}

provider "google" {
  project = "purwadika-441705"
  region  = "asia-southeast2"
}

provider "cloudflare" {
  api_token = "" # Your API token
}

provider "google-beta" {
  project = "purwadika-441705"
  region  = "asia-southeast2"
}

# Set up the Kubernetes provider
provider "kubernetes" {
  host                   = "https://${google_container_cluster.primary.endpoint}"
  cluster_ca_certificate = base64decode(google_container_cluster.primary.master_auth[0].cluster_ca_certificate)
  token                  = data.google_client_config.default.access_token
}

data "google_client_config" "default" {}

provider "helm" {
  kubernetes {
    host                   = "https://${google_container_cluster.primary.endpoint}"
    cluster_ca_certificate = base64decode(google_container_cluster.primary.master_auth[0].cluster_ca_certificate)
    token                  = data.google_client_config.default.access_token
  }
}



resource "google_compute_network" "vpc_network" {
  name                    = "mwc-vpc-network"
  auto_create_subnetworks = false
}

resource "google_compute_subnetwork" "subnet" {
  name          = "mwc-subnet"
  region        = "asia-southeast2"
  network       = google_compute_network.vpc_network.id
  ip_cidr_range = "10.0.0.0/24"
}

resource "google_compute_firewall" "gke_firewall" {
  name    = "gke-firewall"
  network = google_compute_network.vpc_network.id

  allow {
    protocol = "tcp"
    ports    = ["443", "80", "10250"] # Include port 10250 for Kubernetes health checks
  }

  source_ranges = ["0.0.0.0/0"]
}

resource "google_container_cluster" "primary" {
  name                = "primary-mwc-cluster"
  location            = "asia-southeast2-a"
  deletion_protection = false

  network    = google_compute_network.vpc_network.id
  subnetwork = google_compute_subnetwork.subnet.id

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



resource "helm_release" "ingress_nginx" {
  name       = "ingress-nginx-new"  # Use a different name
  chart      = "ingress-nginx"
  repository = "https://kubernetes.github.io/ingress-nginx"
  namespace  = "ingress-nginx"
  create_namespace = true
  skip_crds = true

  values = [
    <<EOF
controller:
  service:
    type: LoadBalancer
EOF
  ]
}




resource "kubernetes_namespace" "github_runner" {
  metadata {
    name = "github-runner"
  }
}

resource "kubernetes_deployment" "github_runner" {
  metadata {
    name      = "github-runner"
    namespace = kubernetes_namespace.github_runner.metadata[0].name
    labels = {
      app = "github-runner"
    }
  }
  spec {
    replicas = 1
    selector {
      match_labels = { app = "github-runner" }
    }
    template {
      metadata {
        labels = { app = "github-runner" }
      }
      spec {
        container {
          name  = "github-runner"
          image = "myoung34/github-runner:latest"
          env {
            name  = "RUNNER_SCOPE"
            value = "repo"
          }
          env {
            name  = "RUNNER_NAME"
            value = "github-runner"
          }
          env {
            name  = "RUNNER_TOKEN"
            value = "" ## Your runner token
          }
          env {
            name  = "REPO_URL"
            value = "https://github.com/fadellh/microservices-ddd-java"
          }
          // Optionally:
          // env {
          //   name  = "RUNNER_REPO"
          //   value = "fadellh/microservices-ddd-java"
          // }
        }
      }
    }
  }
}




#######################################
# 1. API
#######################################
resource "google_api_gateway_api" "this" {
  provider     = google-beta
  api_id       = "my-api"
  display_name = "My API Gateway"
}

#######################################
# 2. API Config
#######################################
resource "google_api_gateway_api_config" "this" {
  provider      = google-beta
  api           = google_api_gateway_api.this.api_id
  api_config_id = "v1"
  # File OpenAPI 2.0/Swagger 2.0
  openapi_documents {
    document {
      path     = "openapi.yaml"
      contents = filebase64("openapi.yaml")
    }
  }

  lifecycle {
    create_before_destroy = true
  }
}

#######################################
# 3. Gateway
#######################################
resource "google_api_gateway_gateway" "this" {
  provider   = google-beta
  gateway_id = "my-gateway"
  api_config = google_api_gateway_api_config.this.id
  region     = "us-central1"
}

#######################################
# 4. Cloudflare DNS Record (Opsional)
#######################################
resource "cloudflare_record" "api_gateway" {
  zone_id = "4e068a601c2e4255aad0c49335e9e3c9"
  name    = "api"
  content = google_api_gateway_gateway.this.default_hostname
  type    = "CNAME"
  ttl     = 300
}


output "ingress_nginx_ip" {
  value = helm_release.ingress_nginx.status
}

output "cloudflare_api_url" {
  value = cloudflare_record.api_gateway.name
}

output "cluster_endpoint" {
  value = google_container_cluster.primary.endpoint
}

# output "cloudflare_api_url" {
#   value = cloudflare_record.api_gateway.name
# }

output "api_gateway_url" {
  value = google_api_gateway_gateway.this.default_hostname
}
