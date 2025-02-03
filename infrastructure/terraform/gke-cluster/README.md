# Terraform Project

This README provides instructions for setting up and deploying this Terraform project. Follow the steps below to configure and deploy resources.

## Prerequisites

1. Install [Terraform](https://www.terraform.io/downloads.html).
2. Ensure you have the necessary credentials and permissions to interact with the required cloud providers (e.g., Google Cloud, Cloudflare).
3. Set up your environment with the following tools if needed:
   - Google Cloud CLI (`gcloud`)
   - Cloudflare API token

## Setting Up the `terraform.tfvars` File

The `terraform.tfvars` file contains the variable values required for this project. Create a new `terraform.tfvars` file in the root directory of this project and populate it with the following content:

```hcl
project                 = "your-google-cloud-project-id"
region                  = "asia-southeast2"
gateway_region          = "asia-northeast1"
cloudflare_zone         = "your-cloudflare-zone-id"
cloudflare_api_token    = "your-cloudflare-api-token"
api_runner_token        = "your-api-runner-token"
region_zone             = "asia-southeast2-a"
kafka_namespace         = "kafka"
kafka_release_name      = "my-kafka-cluster"
replica_count           = 3
zookeeper_replica_count = 3
domain_name             = "your-domain-name.com"
```

### Variable Descriptions

- **`project`**: Your Google Cloud project ID.
- **`region`**: The primary region for deploying resources.
- **`gateway_region`**: The region where the gateway will be deployed.
- **`cloudflare_zone`**: Cloudflare zone ID for DNS management.
- **`cloudflare_api_token`**: API token to interact with Cloudflare (**keep this secure**).
- **`api_runner_token`**: API token for the runner service (**keep this secure**).
- **`region_zone`**: The zone within the region for resource placement.
- **`kafka_namespace`**: Kubernetes namespace for deploying Kafka.
- **`kafka_release_name`**: The Helm release name for the Kafka deployment.
- **`replica_count`**: Number of Kafka replicas.
- **`zookeeper_replica_count`**: Number of Zookeeper replicas.
- **`domain_name`**: The domain name to use for this project.

## Steps to Deploy

1. **Initialize Terraform**:
   Run the following command to initialize Terraform and install required providers:
   ```bash
   terraform init
   ```

2. **Validate the Configuration**:
   Validate the Terraform configuration to ensure everything is correctly set up:
   ```bash
   terraform validate
   ```

3. **Plan the Deployment**:
   Generate and review the Terraform execution plan:
   ```bash
   terraform plan
   ```

4. **Apply the Configuration**:
   Apply the Terraform configuration to deploy resources:
   ```bash
   terraform apply
   ```
   Confirm the apply action when prompted.

5. **Monitor the Deployment**:
   Use the relevant cloud provider tools (e.g., Google Cloud Console, kubectl, or Cloudflare dashboard) to monitor the deployed resources.

## Additional Notes

- Keep the `terraform.tfvars` file secure as it contains sensitive information like API tokens.
- For advanced configurations or customizations, you can modify the `variables.tf` file to add or change variables.
- Avoid committing sensitive information to version control systems (e.g., Git). Use tools like `.gitignore` to exclude `terraform.tfvars` or consider using a secrets management tool.

Edge case:
- `terraform apply -target=module.kafka.helm_release.strimzi_operator`
- `kubectl apply -f schema-registry.yml`
- `kubectl apply -f akhq-deployment.yml`
- manual add external ingress load balancer in cloudflare
---

Redeploy or restart kafka
- Delete current state
```bash
terraform state rm module.kafka.kubernetes_manifest.kafka_cr
terraform state rm module.kafka.helm_release.strimzi_operator
terraform state rm module.kafka.kubernetes_namespace.kafka_ns
```
- Delete namespace
```bash
kubectl delete namespace kafka
```
- Apply 
```bash
terraform apply -target=module.kafka.helm_release.strimzi_operator
terraform apply -target=module.kafka
kubectl apply -f schema-registry.yml
kubectl apply -f akhq-deployment.yml
```




Mengatur cloudflare:
- Gunakan worker & page 
- Pakai code ini
  ```
  export default {
    async fetch(request) {
      const targetUrl = "https://mwc-gateway-ysw27pj.an.gateway.dev";
    
      const newRequest = new Request(request);
      newRequest.headers.set("Host", "mwc-gateway-ysw27pj.an.gateway.dev");
    
      const url = new URL(request.url);
      const path = url.pathname + url.search;
      const target = targetUrl + path;
    
      // Teruskan request ke Google API Gateway
      return fetch(target, newRequest);
    }
    };
  ```
- Pilih worker. Atur custom domain

# API Gateway Configuration Guide

This project manages an API Gateway using Terraform. The API Gateway's routing, security, and rate-limiting rules are defined in an `openapi.yaml` file. Follow the instructions below to set up and maintain this configuration.

## Setting Up `openapi.yaml`

The `openapi.yaml` file defines:
- Endpoints
- Backend routing
- Security mechanisms (e.g., API keys, JWT authentication)
- Rate-limiting rules

mengatur API gateway api key https://cloud.google.com/api-gateway/docs/authenticate-api-keys
```
gcloud services enable mwc-api-g-1rvo4g3euji6h.apigateway.purwadika-441705.cloud.goog
```

### Steps to Configure `openapi.yaml`

1. **Locate the `openapi.yaml` File**
   - Ensure the `openapi.yaml` file is present in the project directory.
   - If it's missing, recreate it using the following template as a starting point:

     ```yaml
     swagger: "2.0"
     info:
       title: "My API Gateway"
       version: "1.0.0"
     host: "your-api-domain.com"
     schemes:
       - https

     paths:
       /protected-resource:
         get:
           summary: "Access protected resource"
           operationId: getProtectedResource
           security:
             - jwt_auth: []
           responses:
             "200":
               description: "Successful response"
               schema:
                 type: object
                 properties:
                   message:
                     type: string
             "401":
               description: "Unauthorized"
             "429":
               description: "Too many requests"
           x-google-backend:
             address: "http://your-backend-service.svc.cluster.local"
           x-google-extensions:
             x-google-ratelimit:
               limit: 100
               interval: 60s
             x-google-jwks_uri:
               jwks_uri: "https://www.googleapis.com/service_accounts/v1/metadata/jwk/secure-token"

     securityDefinitions:
       jwt_auth:
         type: "apiKey"
         name: "x-api-key"
         in: "header"
     ```

2. **Customize the File**
   - Replace `your-api-domain.com` with your actual API domain.
   - Update the backend address (`x-google-backend > address`) to point to the correct backend service.

3. **Ensure Synchronization with Terraform**
   - Terraform will automatically deploy or update the API Gateway with the `openapi.yaml` configuration during its `apply` phase.
   - Place the `openapi.yaml` file in the location specified in your Terraform module (e.g., `modules/api-gateway/openapi.yaml`).

4. **Validate the YAML**
   - Use a linter or online tools (e.g., [Swagger Editor](https://editor.swagger.io/)) to validate the `openapi.yaml` file before deployment.

## Awareness Notes

- **Why is `openapi.yaml` Important?**
  - It defines the core behavior of your API Gateway, including security, routing, and traffic management.
  - Any misconfiguration could lead to security vulnerabilities or service disruptions.

- **When to Update `openapi.yaml`?**
  - Add or modify API routes.
  - Change backend services or domain names.
  - Adjust rate-limiting or authentication settings.

- **Terraform Integration**
  - Ensure the `openapi.yaml` file is synchronized with the Terraform configuration for seamless deployments.

## Testing the API Gateway

After applying changes, test the API Gateway to ensure it behaves as expected:
```bash
curl -X GET "https://your-api-domain.com/protected-resource" \
     -H "x-api-key: <your-api-key>"
```

---