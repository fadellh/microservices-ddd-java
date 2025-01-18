###############################################################################
# MAIN
# - Calls modules
###############################################################################

module "network" {
  source  = "./modules/network"
  project = var.project
  region  = var.region
}

module "firewall" {
  source     = "./modules/firewall"
  project    = var.project
  network_id = module.network.network_id
}

module "kubernetes" {
  source        = "./modules/kubernetes"
  project       = var.project
  region        = var.region
  network_id    = module.network.network_id
  subnetwork_id = module.network.subnetwork_id
  region_zone   = var.region_zone
}

# module "github_runner" {
#   source       = "./modules/github_runner"
#   namespace    = "github-runner"
#   runner_token = var.api_runner_token
# }

module "helm" {
  source           = "./modules/cert_ingress"
  kubernetes_host  = module.kubernetes.cluster_host
  kubernetes_ca    = module.kubernetes.cluster_ca
  kubernetes_token = module.kubernetes.cluster_token
}

module "api_gateway" {
  source         = "./modules/api_gateway"
  project        = var.project
  gateway_region = var.gateway_region

  depends_on = [module.kubernetes]

}

module "dns" {
  source          = "./modules/dns"
  cloudflare_zone = var.cloudflare_zone
  api_hostname    = module.api_gateway.gateway_hostname

  providers = {
    cloudflare = cloudflare
  }

  depends_on = [module.api_gateway]
}


# module "kafka" {
#   source                  = "./modules/kafka"
#   kafka_namespace         = var.kafka_namespace
#   kafka_release_name      = var.kafka_release_name
#   replica_count           = var.replica_count
#   zookeeper_replica_count = var.zookeeper_replica_count
#   domain_name             = var.domain_name
#   providers = {
#     kubernetes = kubernetes.kubeconfig,
#     helm       = helm.kubeconfig
#   }

#   depends_on = [
#     module.kubernetes
#   ]

# }

module "kafka" {
  source                  = "./modules/kafka"
  kafka_namespace         = var.kafka_namespace
  kafka_release_name      = var.kafka_release_name
  replica_count           = var.replica_count
  zookeeper_replica_count = var.zookeeper_replica_count
  domain_name             = var.domain_name

  providers = {
    kubernetes = kubernetes
    helm       = helm
  }

  depends_on = [module.kubernetes]
}

