package ru.development.infrastructurekafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.development.infrastructurekafka.model.GigaChatProducerInfo;

@Slf4j
@Service
class GigachatConsumer {

    @KafkaListener(topics = "gigachat.message")
    public void consumeOrder(ConsumerRecord<String, GigaChatProducerInfo> record) {
        log.info("[KAFKA INFO] Received GigaChatProducerInfo: {}", record.value());
    }

}
