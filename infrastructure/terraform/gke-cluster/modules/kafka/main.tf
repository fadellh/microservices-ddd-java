# Include namespace, Strimzi operator, and Kafka cluster in the same file
resource "kubernetes_namespace" "kafka_ns" {
  metadata {
    name = var.kafka_namespace
  }
}

resource "helm_release" "strimzi_operator" {

  name       = "strimzi-operator"
  namespace  = kubernetes_namespace.kafka_ns.metadata[0].name
  repository = "https://strimzi.io/charts/"
  chart      = "strimzi-kafka-operator"
  version    = "0.38.0"

  set {
    name  = "installCRDs"
    value = "true"
  }

  wait    = true
  timeout = 300

  cleanup_on_fail = true
  recreate_pods   = false
}

resource "kubernetes_manifest" "kafka_cr" {
  manifest = {
    "apiVersion" = "kafka.strimzi.io/v1beta2"
    "kind"       = "Kafka"
    "metadata" = {
      "name"      = var.kafka_release_name
      "namespace" = kubernetes_namespace.kafka_ns.metadata[0].name
    }
    "spec" = {
      "kafka" = {
        "version"  = "3.5.0"
        "replicas" = var.replica_count
        "config" = {
          "default.replication.factor" = 3
          "min.insync.replicas"        = 2
        }
        "listeners" = [
          {
            "name" = "plain"
            "port" = 9092
            "type" = "internal"
            "tls"  = false
          },
          {
            "name" = "tls"
            "port" = 9093
            "type" = "internal"
            "tls"  = true
          },
          {
            "name" = "external"
            "port" = 9094
            "type" = "nodeport"
            "tls"  = false
            "configuration" = {
              "bootstrap" = {
                "nodePort" = "32100"
              }
              "brokers" = [
                { "broker" = 0, "nodePort" = "32000" },
                { "broker" = 1, "nodePort" = "32001" },
                { "broker" = 2, "nodePort" = "32002" }
              ]
            }
          }
        ]
        "storage" = {
          "type" = "jbod"
          "volumes" = [
            {
              "id"    = 0
              "type"  = "persistent-claim"
              "size"  = "10Gi"
              "class" = "standard"
            }
          ]
        }
      }

      "zookeeper" = {
        "replicas" = var.zookeeper_replica_count
        "storage" = {
          "type"  = "persistent-claim"
          "size"  = "10Gi"
          "class" = "standard"
        }
      }

      "entityOperator" = {
        "topicOperator" = {}
        "userOperator"  = {}
      }
    }
  }

  depends_on = [
    helm_release.strimzi_operator
  ]
}
