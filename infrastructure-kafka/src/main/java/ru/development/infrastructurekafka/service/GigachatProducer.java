package ru.development.infrastructurekafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final KafkaTemplate<String, Object> deadLetterKafkaTemplate;

    @MetaDataInfo(value = "gigachat-message-producer")
    public void sendMainMessage(String topic, String key, GigaChatRequestData gigaChatRequestData) {
        log.info("[KAFKA INFO] Отправка основного GigaChat сообщения: {}", gigaChatRequestData);
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                topic, key, gigaChatRequestData
        );
        log.info("[KAFKA INFO] Sending Giga-Chat-main-message ProducerRecord {}", producerRecord);
        deadLetterKafkaTemplate.send(producerRecord);
    }

    @MetaDataInfo("gigachat-info-producer")
    public void sendInfoMessage(String topic, String key, GigaChatProducerInfo gigaChatProducerInfo) {
        log.info("[KAFKA INFO] Sending GigaChatProducerInfo: {}", gigaChatProducerInfo);
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
                topic, key, gigaChatProducerInfo
        );
        log.info("[KAFKA INFO] Sending Giga-chat-info ProducerRecord {}", producerRecord);

        deadLetterKafkaTemplate.send(producerRecord);
    }
}
