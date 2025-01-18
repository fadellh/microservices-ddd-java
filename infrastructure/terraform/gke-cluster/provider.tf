###############################################################################
# PROVIDER CONFIG
###############################################################################
# terraform {
#   required_providers {
#     google = {
#       source  = "hashicorp/google"
#       version = ">= 4.50.0"
#     }
#     google-beta = {
#       source  = "hashicorp/google-beta"
#       version = ">= 4.50.0"
#     }
#     cloudflare = {
#       source  = "cloudflare/cloudflare"
#       version = "~> 4.0"
#     }
#     helm = {
#       source  = "hashicorp/helm"
#       version = "~> 2.0"
#     }
#     kubernetes = {
#       source  = "hashicorp/kubernetes"
#       version = "~> 2.0"
#     }
#   }
# }

# provider "google" {
#   project = var.project
#   region  = var.region
# }

# provider "google-beta" {
#   project = var.project
#   region  = var.region
# }

# provider "cloudflare" {
#   api_token = var.cloudflare_api_token
# }


# provider "kubernetes" {
#   alias                  = "kubeconfig"
#   host                   = module.kubernetes.cluster_host
#   cluster_ca_certificate = module.kubernetes.cluster_ca
#   token                  = module.kubernetes.cluster_token
# }

# provider "helm" {
#   alias = "kubeconfig"
#   kubernetes {
#     host                   = module.kubernetes.cluster_host
#     cluster_ca_certificate = module.kubernetes.cluster_ca
#     token                  = module.kubernetes.cluster_token
#   }
# }

# data "google_client_config" "default" {}

# provider "kubernetes" {
#   # Instead of referencing google_container_cluster.primary, use the module outputs
#   host                   = "https://${module.kubernetes.cluster_host}"
#   cluster_ca_certificate = module.kubernetes.cluster_ca
#   token                  = module.kubernetes.cluster_token
# }

# provider "helm" {
#   kubernetes {
#     host                   = "https://${module.kubernetes.cluster_host}"
#     cluster_ca_certificate = module.kubernetes.cluster_ca
#     token                  = module.kubernetes.cluster_token
#   }
# }



###############################################################################
# PROVIDER CONFIG
###############################################################################
terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = ">= 4.50.0"
    }
    google-beta = {
      source  = "hashicorp/google-beta"
      version = ">= 4.50.0"
    }
    cloudflare = {
      source  = "cloudflare/cloudflare"
      version = "~> 4.0"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.0"
    }
  }
}

provider "google" {
  project = var.project
  region  = var.region
}

provider "google-beta" {
  project = var.project
  region  = var.region
}

provider "cloudflare" {
  api_token = var.cloudflare_api_token
}

data "google_client_config" "default" {}

# The single "kubernetes" provider that references your GKE cluster outputs.
# Note: if module.kubernetes.cluster_host is just an IP (like 34.101.221.126),
# prefix it with "https://". If module.kubernetes already returns "https://<IP>",
# then remove the extra "https://" below to avoid duplicates.
provider "kubernetes" {
  host                   = "https://${module.kubernetes.cluster_host}"
  cluster_ca_certificate = module.kubernetes.cluster_ca
  token                  = module.kubernetes.cluster_token
}

# The single "helm" provider referencing the same cluster.
provider "helm" {
  kubernetes {
    host                   = "https://${module.kubernetes.cluster_host}"
    cluster_ca_certificate = module.kubernetes.cluster_ca
    token                  = module.kubernetes.cluster_token
  }
}
