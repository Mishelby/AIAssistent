package ru.development.infrastructurekafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.development.infrastructurekafka.model.GigaChatProducerInfo;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GigachatProducer {
    private final KafkaTemplate<String, GigaChatProducerInfo> kafkaTemplate;

    public void sendMessage(GigaChatProducerInfo gigaChatProducerInfo) {
        log.info("[KAFKA INFO] Sending GigaChatProducerInfo: {}", gigaChatProducerInfo);
        ProducerRecord<String, GigaChatProducerInfo> producerRecord = new ProducerRecord<>(
                "gigachat.message", gigaChatProducerInfo.key(), gigaChatProducerInfo
        );

        String correlationId = UUID.randomUUID().toString();
        producerRecord.headers().add("correlationId", correlationId.getBytes(StandardCharsets.UTF_8));
        log.info("[KAFKA INFO] Sending ProducerRecord with correlationId: {}", correlationId);

        log.info("[KAFKA INFO] Sending ProducerRecord: {}", producerRecord);
        kafkaTemplate.send(producerRecord);
    }
}
