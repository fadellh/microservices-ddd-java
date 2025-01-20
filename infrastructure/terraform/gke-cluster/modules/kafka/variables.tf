variable "kafka_namespace" {
  type        = string
  description = "Namespace for Strimzi Kafka and related components."
}

variable "kafka_release_name" {
  type        = string
  description = "Unique name for the Kafka CR in Strimzi."
}

variable "replica_count" {
  type        = number
  description = "Number of Kafka broker replicas."
}

variable "zookeeper_replica_count" {
  type        = number
  description = "Number of Zookeeper replicas."
}

variable "domain_name" {
  type        = string
  description = "Your primary domain."
}
