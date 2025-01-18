###############################################################################
# VARIABLES
###############################################################################
variable "project" {
  description = "Google Cloud project ID"
  type        = string
}

variable "region" {
  description = "Google Cloud region for cluster/network"
  type        = string
  default     = "asia-southeast2"
}

variable "region_zone" {
  description = "value of region zone"
  type        = string
}

variable "gateway_region" {
  description = "Region for the API Gateway"
  type        = string
  default     = "asia-northeast1"
}

variable "cloudflare_zone" {
  description = "Cloudflare Zone ID"
  type        = string
}

variable "cloudflare_api_token" {
  description = "Cloudflare API Token"
  type        = string
  sensitive   = true
}

variable "api_runner_token" {
  description = "GitHub Runner token"
  type        = string
  sensitive   = true
}

variable "kafka_namespace" {
  description = "Kafka namespace"
  type        = string
}

variable "kafka_release_name" {
  description = "Kafka release name"
  type        = string
}

variable "replica_count" {
  description = "Number of Kafka replicas"
  type        = number
}

variable "zookeeper_replica_count" {
  description = "Number of Zookeeper replicas"
  type        = number
}

variable "domain_name" {
  description = "Domain name"
  type        = string
}




