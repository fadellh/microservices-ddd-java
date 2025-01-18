###############################################################################
# OUTPUTS
###############################################################################
output "github_runner_namespace" {
  description = "The namespace where the GitHub runner is deployed"
  value       = kubernetes_namespace.github_runner.metadata[0].name
}

# output "github_runner_deployment" {
#   description = "The name of the GitHub runner deployment"
#   value       = kubernetes_deployment.github_runner.metadata[0].name
# }

output "github_runner_fe_deployment" {
  description = "The name of the FE GitHub runner deployment"
  value       = kubernetes_deployment.github_runner-fe.metadata[0].name
}
