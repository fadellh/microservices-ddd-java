###############################################################################
# 1. API
###############################################################################
resource "google_api_gateway_api" "this" {
  provider     = google-beta
  api_id       = "mwc-api-g"
  display_name = "MWC API Gateway"
}

###############################################################################
# 2. API Config
###############################################################################
resource "google_api_gateway_api_config" "this" {
  provider      = google-beta
  api           = google_api_gateway_api.this.api_id
  api_config_id = "v9"

  # File OpenAPI 2.0/Swagger 2.0
  openapi_documents {
    document {
      path     = "openapi.yaml"
      contents = filebase64("openapi.yaml")
    }
  }

  lifecycle {
    create_before_destroy = true
  }
}

###############################################################################
# 3. Gateway
###############################################################################
resource "google_api_gateway_gateway" "this" {
  provider   = google-beta
  gateway_id = "mwc-gateway"
  api_config = google_api_gateway_api_config.this.id
  region     = var.gateway_region
}

# output "gateway_hostname" {
#   value = google_api_gateway_gateway.this.default_hostname
# }
