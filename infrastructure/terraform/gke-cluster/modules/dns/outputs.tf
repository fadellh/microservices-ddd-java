output "cf_record_name" {
  description = "Name of the Cloudflare DNS record"
  value       = cloudflare_record.api_gateway.name
}
