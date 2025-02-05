# Terraform & API Gateway Project

This document provides comprehensive instructions for setting up, configuring, and deploying infrastructure using Terraform as well as managing the API Gateway configuration. Follow the steps below to configure and deploy resources on your cloud provider (e.g., Google Cloud) and Cloudflare.

---

## Table of Contents

1. [Terraform Project Setup](#terraform-project-setup)  
   1.1. [Prerequisites](#prerequisites)  
   1.2. [Setting Up `terraform.tfvars`](#setting-up-terraformtfvars)  
   1.3. [Steps to Deploy](#steps-to-deploy)  
   1.4. [Edge Cases & Kafka Redeployment](#edge-cases--kafka-redeployment)
2. [Cloudflare Configuration](#cloudflare-configuration)
3. [API Gateway Configuration Guide](#api-gateway-configuration-guide)
4. [Additional Notes](#additional-notes)

---

## Terraform Project Setup

This section provides instructions for setting up and deploying the Terraform project to provision your infrastructure resources.

### Prerequisites

1. **Install [Terraform](https://www.terraform.io/downloads.html).**
2. Ensure you have the necessary credentials and permissions for your cloud providers (e.g., Google Cloud, Cloudflare).
3. Install additional tools if needed:
   - [Google Cloud CLI (`gcloud`)](https://cloud.google.com/sdk/docs/install)
   - Cloudflare API token (for DNS management and workers)

### Setting Up `terraform.tfvars`

Create a new `terraform.tfvars` file in the root directory of the project and populate it with the following content:

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

#### Variable Descriptions

- **`project`**: Your Google Cloud project ID.
- **`region`**: The primary region for deploying resources.
- **`gateway_region`**: The region where the API gateway will be deployed.
- **`cloudflare_zone`**: Cloudflare zone ID for DNS management.
- **`cloudflare_api_token`**: API token for Cloudflare (**keep this secure**).
- **`api_runner_token`**: API token for the runner service (**keep this secure**).
- **`region_zone`**: The specific zone within the region for resource placement.
- **`kafka_namespace`**: Kubernetes namespace for deploying Kafka.
- **`kafka_release_name`**: Helm release name for the Kafka deployment.
- **`replica_count`**: Number of Kafka replicas.
- **`zookeeper_replica_count`**: Number of Zookeeper replicas.
- **`domain_name`**: The domain name used for the project.

### Steps to Deploy

1. **Initialize Terraform**  
   Run the following command to initialize Terraform and install the required providers:

   ```bash
   terraform init
   ```

2. **Validate the Configuration**  
   Validate the Terraform configuration to ensure everything is set up correctly:

   ```bash
   terraform validate
   ```

3. **Plan the Deployment**  
   Generate and review the Terraform execution plan:

   ```bash
   terraform plan
   ```

4. **Apply the Configuration**  
   Apply the Terraform configuration to deploy your resources:

   ```bash
   terraform apply
   ```

   Confirm the apply action when prompted.

5. **Monitor the Deployment**  
   Use your cloud provider's tools (e.g., Google Cloud Console, `kubectl`, Cloudflare dashboard) to monitor the deployed resources.

### Edge Cases & Kafka Redeployment

If you need to target only specific modules or restart Kafka, follow these steps:

#### Applying Specific Targets

- Apply only the Strimzi operator:

  ```bash
  terraform apply -target=module.kafka.helm_release.strimzi_operator
  ```

- Then apply additional Kubernetes manifests:

  ```bash
  kubectl apply -f schema-registry.yml
  kubectl apply -f akhq-deployment.yml
  ```

#### Redeploy or Restart Kafka

1. **Delete the Current State**  
   Remove the Kafka-related resources from Terraform state:

   ```bash
   terraform state rm module.kafka.kubernetes_manifest.kafka_cr
   terraform state rm module.kafka.helm_release.strimzi_operator
   terraform state rm module.kafka.kubernetes_namespace.kafka_ns
   ```

2. **Delete the Namespace**  
   Delete the Kafka namespace from your Kubernetes cluster:

   ```bash
   kubectl delete namespace kafka
   ```

3. **Reapply the Configuration**  
   Redeploy the Kafka modules:

   ```bash
   terraform apply -target=module.kafka.helm_release.strimzi_operator
   terraform apply -target=module.kafka
   kubectl apply -f schema-registry.yml
   kubectl apply -f akhq-deployment.yml
   ```

---

## Cloudflare Configuration

To manage external ingress via Cloudflare, configure a Cloudflare Worker with the following code. This worker forwards requests to your Google API Gateway:

```js
export default {
  async fetch(request) {
    const targetUrl = "https://mwc-gateway-ysw27pj.an.gateway.dev";
  
    const newRequest = new Request(request);
    newRequest.headers.set("Host", "mwc-gateway-ysw27pj.an.gateway.dev");
  
    const url = new URL(request.url);
    const path = url.pathname + url.search;
    const target = targetUrl + path;
  
    // Forward the request to the Google API Gateway
    return fetch(target, newRequest);
  }
};
```

- **Deployment Steps**:
  1. Create a new Cloudflare Worker.
  2. Paste the above code.
  3. Configure a custom domain using Cloudflare Pages or the Worker’s settings.
  4. Ensure you have the necessary API tokens and Cloudflare zone configured in your Terraform variables.

---

## API Gateway Configuration Guide

This section explains how to set up and maintain the API Gateway using Terraform and an `openapi.yaml` file.

### Setting Up `openapi.yaml`

The `openapi.yaml` file defines:

- API endpoints and backend routing.
- Security mechanisms (e.g., API keys, JWT authentication).
- Rate-limiting rules.

#### Template Example

If you don’t have an `openapi.yaml` file, create one using the template below:

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

#### Steps to Configure

1. **Locate and Customize the File**  
   - Replace `your-api-domain.com` with your actual domain.
   - Update the backend address (`x-google-backend > address`) to point to your correct backend service.
   
2. **Synchronize with Terraform**  
   - Place the `openapi.yaml` file in the location specified by your Terraform module (e.g., `modules/api-gateway/openapi.yaml`).
   - Terraform will automatically deploy or update the API Gateway configuration during `terraform apply`.

3. **Validate the YAML**  
   - Use tools like [Swagger Editor](https://editor.swagger.io/) to validate the file before deployment.

### Testing the API Gateway

After deployment, test the API Gateway with:

```bash
curl -X GET "https://your-api-domain.com/protected-resource" \
     -H "x-api-key: <your-api-key>"
```

### Enabling API Key Authentication

Enable API key authentication for your API Gateway using the following command:

```bash
gcloud services enable mwc-api-g-1rvo4g3euji6h.apigateway.purwadika-441705.cloud.goog
```

---

## Additional Notes

- **Security**:  
  - Keep the `terraform.tfvars` file secure as it contains sensitive information.
  - Exclude sensitive files from version control (e.g., add `terraform.tfvars` to `.gitignore`).

- **Customization**:  
  - Modify `variables.tf` for advanced configurations or to add custom variables.
  - Update the `openapi.yaml` file whenever you change API endpoints, security, or routing rules.

- **Deployment Automation**:  
  - Use CI/CD pipelines for automated deployments.
  - Refer to the provided scripts and documentation for data migration and load testing.

---

Happy deploying! If you have any questions or encounter issues, please open a GitHub issue or contact the maintainers.