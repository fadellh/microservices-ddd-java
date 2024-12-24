package com.mwc.kafka;

import com.mwc.kafka.config.data.KafkaConfigData;
import com.mwc.kafka.config.data.KafkaProducerConfigData;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaProducerConfig<K extends Serializable, V extends SpecificRecordBase> {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaProducerConfigData kafkaProducerConfigData;

    public KafkaProducerConfig(KafkaConfigData kafkaConfigData,
                               KafkaProducerConfigData kafkaProducerConfigData) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaProducerConfigData = kafkaProducerConfigData;
    }

    @Bean
    public Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();

        // 1) Basic Confluent (or any Kafka) connection
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());

        // 2) If using Confluent's Schema Registry
        // The key is something like "schema.registry.url"
        props.put(kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl());
//        props.put(kafkaConfigData.getSchemaRegistryUrl(), kafkaConfigData.getSchemaRegistryUrl());
//        props.put("basic.auth.credentials.source", "USER_INFO");
//        props.put("schema.registry.basic.auth.user.info", kafkaConfigData.getSchemaRegistryBasicAuthUserInfo());

//        props.put("schema.registry.url", kafkaConfigData.getSchemaRegistryUrl());
        props.put(kafkaConfigData.getSchemaRegistryUserInfoKey(), kafkaConfigData.getSchemaRegistryUserInfo());
        props.put(kafkaConfigData.getSchemaRegistryBasicAuthUserInfoKey(), kafkaConfigData.getSchemaRegistryBasicAuthUserInfo());


        // 3) Producer serialization configs
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerConfigData.getKeySerializerClass());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerConfigData.getValueSerializerClass());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG,
                kafkaProducerConfigData.getBatchSize() * kafkaProducerConfigData.getBatchSizeBoostFactor());
        props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfigData.getLingerMs());
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProducerConfigData.getCompressionType());
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfigData.getAcks());
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerConfigData.getRequestTimeoutMs());
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerConfigData.getRetryCount());

        // 4) Security for Kafka (SASL_SSL, PLAIN, etc.)
        props.put("security.protocol", kafkaConfigData.getSecurityProtocol());
        props.put("sasl.mechanism", kafkaConfigData.getSaslMechanism());
        props.put("sasl.jaas.config", kafkaConfigData.getSaslJaasConfig());

        log.info("ProducerConfig => {}", props);
        return props;
    }

    @Bean
    public ProducerFactory<K, V> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<K, V> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
