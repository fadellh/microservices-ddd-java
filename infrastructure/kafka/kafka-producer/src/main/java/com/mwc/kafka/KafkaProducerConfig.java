package com.mwc.kafka;

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

    /**
     * Just the producer config data (no need for KafkaConfigData if
     * you don't use SASL or a Confluent schema registry).
     */
    private final KafkaProducerConfigData kafkaProducerConfigData;

    public KafkaProducerConfig(KafkaProducerConfigData kafkaProducerConfigData) {
        this.kafkaProducerConfigData = kafkaProducerConfigData;
    }

    @Bean
    public Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();

        // REQUIRED: BootStrap servers
        // Put your Kafka broker(s) here, e.g. "PLAINTEXT://my-kafka:9092"
        // or from an env var if you want. Hard-coded below just as example:
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "34.101.89.132:32100");

        // REQUIRED: Key/Value serializers
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//                kafkaProducerConfigData.getKeySerializerClass());
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//                kafkaProducerConfigData.getValueSerializerClass());
//
//        // Additional producer properties
//        props.put(ProducerConfig.BATCH_SIZE_CONFIG,
//                kafkaProducerConfigData.getBatchSize() * kafkaProducerConfigData.getBatchSizeBoostFactor());
//        props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfigData.getLingerMs());
//        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProducerConfigData.getCompressionType());
//        props.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfigData.getAcks());
//        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerConfigData.getRequestTimeoutMs());
//        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerConfigData.getRetryCount());


        putIfNotNull(props, ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "34.101.89.132:32100");
        putIfNotNull(props, ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                kafkaProducerConfigData.getKeySerializerClass());
        putIfNotNull(props, ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                kafkaProducerConfigData.getValueSerializerClass());
        putIfNotNull(props, ProducerConfig.BATCH_SIZE_CONFIG,
                kafkaProducerConfigData.getBatchSize() * kafkaProducerConfigData.getBatchSizeBoostFactor());
        putIfNotNull(props, ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfigData.getLingerMs());
        putIfNotNull(props, ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProducerConfigData.getCompressionType());
        putIfNotNull(props, ProducerConfig.ACKS_CONFIG, kafkaProducerConfigData.getAcks());
        putIfNotNull(props, ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerConfigData.getRequestTimeoutMs());
        putIfNotNull(props, ProducerConfig.RETRIES_CONFIG, kafkaProducerConfigData.getRetryCount());

        // NO schema registry, NO SASL, so no chance of putting null keys or values:
        // (All removed to avoid the null key bug.)

        System.out.println("SYSTEM ProducerConfig ");
        log.info("ProducerConfig => {}", props);
        log.info("Creating NEW ProducerConfig ");
        System.out.println("Creating NEW ProducerConfig ");
        Map<String, Object> newProps = new HashMap<>();
        props.forEach((key, value) -> {
            if (key != null && value != null) {
                newProps.put(key, value);
            }
        });

        log.info("NEW ProducerConfig => {}", newProps);
        return newProps;
    }

    @Bean
    public ProducerFactory<K, V> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (key == null) {
            log.warn("Ignoring config entry with null key. value={}", value);
        } else if (value == null) {
            log.warn("Ignoring config entry for key={} because value is null", key);
        } else {
            map.put(key, value);
        }
    }

    @Bean
    public KafkaTemplate<K, V> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
