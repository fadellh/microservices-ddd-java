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

        // 1) Basic Kafka connection (required)
        putIfNotNull(props, ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());

        // 2) Schema Registry (if applicable)
        putIfNotNull(props, kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl());
        putIfNotNull(props, kafkaConfigData.getSchemaRegistryUserInfoKey(), kafkaConfigData.getSchemaRegistryUserInfo());
        putIfNotNull(props, kafkaConfigData.getSchemaRegistryBasicAuthUserInfoKey(),
                kafkaConfigData.getSchemaRegistryBasicAuthUserInfo());

        // 3) Producer serialization configs (these typically must NOT be null)
        putIfNotNull(props, ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerConfigData.getKeySerializerClass());
        putIfNotNull(props, ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerConfigData.getValueSerializerClass());

        props.put(ProducerConfig.BATCH_SIZE_CONFIG,
                kafkaProducerConfigData.getBatchSize() * kafkaProducerConfigData.getBatchSizeBoostFactor());
        props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfigData.getLingerMs());
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProducerConfigData.getCompressionType());
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfigData.getAcks());
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerConfigData.getRequestTimeoutMs());
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerConfigData.getRetryCount());

        // 4) Security (SASL_SSL, PLAIN, etc.) - only put if they're non-null
        putIfNotNull(props, "security.protocol", kafkaConfigData.getSecurityProtocol());
        putIfNotNull(props, "sasl.mechanism", kafkaConfigData.getSaslMechanism());
        putIfNotNull(props, "sasl.jaas.config", kafkaConfigData.getSaslJaasConfig());

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


    private void putIfNotNull(Map<String, Object> props, String key, Object value) {
        if (key != null && value != null) {
            props.put(key, value);
        }
    }
}
