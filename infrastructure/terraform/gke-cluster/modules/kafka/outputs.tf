output "kafka_namespace" {
  description = "Namespace tempat Strimzi Kafka dan Kafdrop dideploy"
  value       = kubernetes_namespace.kafka_ns.metadata[0].name
}

output "strimzi_operator_release" {
  description = "Nama Helm Release Strimzi Operator"
  value       = helm_release.strimzi_operator.name
}
