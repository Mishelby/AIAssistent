package ru.development.infrastructurekafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.development.infrastructurekafka.model.GigaChatProducerInfo;

@Slf4j
@Service
class GigachatConsumer {

    @KafkaListener(groupId = "default-group", topics = "gigachat.message")
    public void consumeOrder(@Payload ConsumerRecord<String, GigaChatProducerInfo> record,
                             @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                             @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                             @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                             @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts,
                             @Header(KafkaHeaders.CORRELATION_ID) String correlationId) {
        log.info("[KAFKA INFO] Received GigaChatProducerInfo: {}", record.value());
        log.info("[KAFKA INFO] Received Headers: {}, {}, {}, {}", key,  partition, topic, ts);
    }

}
