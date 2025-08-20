package ru.development.infrastructurekafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import ru.development.infrastructurekafka.aop.MetaDataInfo;
import ru.development.infrastructurekafka.model.CheckTokenRequest;
import ru.development.infrastructurekafka.model.GigaChatProducerInfo;
import ru.development.infrastructurekafka.model.GigaChatRequestData;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GigachatProducer {
    private final KafkaTemplate<String, GigaChatProducerInfo> kafkaChatInfoTemplate;
    private final KafkaTemplate<String, GigaChatRequestData> kafkaGigaChatTemplate;
    private final ReplyingKafkaTemplate<String, CheckTokenRequest, GigaChatRequestData> replyingKafkaTemplate;

    @MetaDataInfo(value = "gigachat-message-producer")
    public void sendMainMessage(String topic, String key, GigaChatRequestData gigaChatRequestData) {
        log.info("[KAFKA INFO] Отправка основного GigaChat сообщения: {}", gigaChatRequestData);
        ProducerRecord<String, GigaChatRequestData> producerRecord = new ProducerRecord<>(
                topic, key, gigaChatRequestData
        );
        log.info("[KAFKA INFO] Sending Giga-Chat-main-message ProducerRecord {}", producerRecord);

        kafkaGigaChatTemplate.send(producerRecord);
    }

    @MetaDataInfo("gigachat-info-producer")
    public void sendInfoMessage(String topic, String key, GigaChatProducerInfo gigaChatProducerInfo) {
        log.info("[KAFKA INFO] Sending GigaChatProducerInfo: {}", gigaChatProducerInfo);
        ProducerRecord<String, GigaChatProducerInfo> producerRecord = new ProducerRecord<>(
                topic, key, gigaChatProducerInfo
        );
        log.info("[KAFKA INFO] Sending Giga-chat-info ProducerRecord {}", producerRecord);

        kafkaChatInfoTemplate.send(producerRecord);
    }

    // Это пока не используется
    @MetaDataInfo("token-info-producer")
    public RequestReplyFuture<String, CheckTokenRequest, GigaChatRequestData> checkTokenInfo(
            String topic,
            CheckTokenRequest checkTokenRequest
    ) {
        log.info("[KAFKA INFO] Отправка информации о GigaChat: {}", checkTokenRequest);
        ProducerRecord<String, CheckTokenRequest> producerRecord = new ProducerRecord<>(
                topic, checkTokenRequest.key(), checkTokenRequest
        );

        String correlationId = UUID.randomUUID().toString();
        producerRecord.headers().add(KafkaHeaders.CORRELATION_ID, correlationId.getBytes(StandardCharsets.UTF_8));
        log.info("[KAFKA INFO] Sending Giga-chat-info ProducerRecord {} with correlationId: {}",
                producerRecord, correlationId);

        return replyingKafkaTemplate.sendAndReceive(producerRecord);
    }

}
