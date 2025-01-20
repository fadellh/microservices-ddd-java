output "gateway_hostname" {
  description = "Hostname of the API Gateway"
  value       = google_api_gateway_gateway.this.default_hostname
}
