###############################################################################
# HELM PROVIDER (INLINE) 
# Using dynamic provider config from inputs
###############################################################################
provider "helm" {
  kubernetes {
    host                   = var.kubernetes_host
    cluster_ca_certificate = var.kubernetes_ca
    token                  = var.kubernetes_token
  }
}

###############################################################################
# INGRESS NGINX DEPLOYMENT
###############################################################################
resource "helm_release" "ingress_nginx" {
  name             = "ingress-nginx"
  chart            = "ingress-nginx"
  repository       = "https://kubernetes.github.io/ingress-nginx"
  namespace        = "ingress-nginx"
  create_namespace = true
  skip_crds        = true

  values = [
    <<EOF
controller:
  service:
    type: LoadBalancer
EOF
  ]
}

###############################################################################
# CERT MANAGER DEPLOYMENT
###############################################################################
resource "helm_release" "cert_manager" {
  name             = "cert-manager"
  chart            = "cert-manager"
  repository       = "https://charts.jetstack.io"
  namespace        = "cert-manager"
  create_namespace = true

  set {
    name  = "installCRDs"
    value = "true"
  }
}
