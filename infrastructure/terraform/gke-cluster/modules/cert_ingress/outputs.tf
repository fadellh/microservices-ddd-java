###############################################################################
# OUTPUTS
###############################################################################
output "ingress_nginx_release" {
  description = "Ingress Nginx release name"
  value       = helm_release.ingress_nginx.name
}

output "cert_manager_release" {
  description = "Cert Manager release name"
  value       = helm_release.cert_manager.name
}
