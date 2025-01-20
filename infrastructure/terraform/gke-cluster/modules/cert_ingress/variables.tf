variable "kubernetes_host" {
  description = "The Kubernetes cluster endpoint"
  type        = string
}

variable "kubernetes_ca" {
  description = "The Kubernetes cluster CA certificate"
  type        = string
}

variable "kubernetes_token" {
  description = "The access token for the Kubernetes cluster"
  type        = string
  sensitive   = true
}
