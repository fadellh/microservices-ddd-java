resource "cloudflare_record" "api_gateway" {
  zone_id = var.cloudflare_zone
  name    = "api"
  content = var.api_hostname
  type    = "CNAME"
  ttl     = 300
}


# output "cf_record_name" {
#   description = "Name of the Cloudflare DNS record"
#   value       = cloudflare_record.api_gateway.name
# }
