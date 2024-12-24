package com.mwc.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaMessageHelper {
    public <K, V> BiConsumer<SendResult<K, V>, Throwable>
    getKafkaCallback(String responseTopicName, V avroModel, String key, String avroModelName) {
        return (result, ex) -> {
            if (ex != null) {
                log.error("Error while sending {} message {} to topic {}", avroModelName,
                        avroModel.toString(), responseTopicName, ex);
            } else {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Received successful response from Kafka for key: {}" +
                                " Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        key,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp());
            }
        };
    }
}
