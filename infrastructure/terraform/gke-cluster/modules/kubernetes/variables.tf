variable "project" {
  description = "Google Cloud project ID"
  type        = string
}

variable "region" {
  description = "Google Cloud region"
  type        = string
}

variable "region_zone" {
  description = "Google Cloud region zone"
  type        = string
}

variable "network_id" {
  description = "The ID of the VPC network"
  type        = string
}

variable "subnetwork_id" {
  description = "The ID of the subnet"
  type        = string
}
