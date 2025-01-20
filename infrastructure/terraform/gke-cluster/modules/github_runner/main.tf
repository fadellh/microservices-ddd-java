###############################################################################
# GITHUB RUNNER NAMESPACE
###############################################################################
resource "kubernetes_namespace" "github_runner" {
  metadata {
    name = var.namespace
  }
}

###############################################################################
# GITHUB RUNNER DEPLOYMENT
###############################################################################
# resource "kubernetes_deployment" "github_runner" {
#   metadata {
#     name      = var.namespace
#     namespace = kubernetes_namespace.github_runner.metadata[0].name
#     labels = {
#       app = "github-runner"
#     }
#   }

#   spec {
#     replicas = 1
#     selector {
#       match_labels = { app = "github-runner" }
#     }

#     template {
#       metadata {
#         labels = { app = "github-runner" }
#       }

#       spec {
#         container {
#           name  = "github-runner"
#           image = "myoung34/github-runner:latest"

#           command = ["/bin/sh", "-c"]
#           args = [
#             "apt-get update && apt-get install -y docker.io"
#           ]

#           env {
#             name  = "RUNNER_SCOPE"
#             value = "repo"
#           }

#           env {
#             name  = "RUNNER_NAME"
#             value = "github-runner"
#           }

#           env {
#             name  = "RUNNER_TOKEN"
#             value = var.runner_token
#           }

#           # Example: Adjust with your GitHub repo
#           env {
#             name  = "REPO_URL"
#             value = "https://github.com/fadellh/microservices-ddd-java"
#           }
#         }
#       }
#     }
#   }
# }


resource "kubernetes_deployment" "github_runner-fe" {
  metadata {
    name      = var.namespace
    namespace = kubernetes_namespace.github_runner.metadata[0].name
    labels = {
      app = "github-runner-fe"
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = { app = "github-runner-fe" }
    }

    template {
      metadata {
        labels = { app = "github-runner-fe" }
      }

      spec {
        container {
          name  = "github-runner-fe"
          image = "myoung34/github-runner:latest"

          # command = ["/bin/sh", "-c"]
          # args = [
          #   "apt-get update && apt-get install -y docker.io"
          # ]

          env {
            name  = "RUNNER_SCOPE"
            value = "repo"
          }

          env {
            name  = "RUNNER_NAME"
            value = "github-runner-fe"
          }

          env {
            name  = "RUNNER_TOKEN"
            value = "AOOIZYYFIBGALDAM7A573RTHRKEUI"
          }

          # Example: Adjust with your GitHub repo
          env {
            name  = "REPO_URL"
            value = "https://github.com/fadellh/ecommerce-react"
          }
        }
      }
    }
  }
}
