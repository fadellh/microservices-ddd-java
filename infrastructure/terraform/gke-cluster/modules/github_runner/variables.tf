variable "namespace" {
  description = "Namespace for GitHub runner"
  type        = string
  default     = "github-runner"
}

variable "runner_token" {
  description = "GitHub runner registration token"
  type        = string
  sensitive   = true
}
