package ru.development.main.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.development.main.service.GigaChatService;
import ru.development.infrastructurekafka.model.GigaChatRequestData;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class GigaChatMessageConsumer {
    private final GigaChatService gigaChatService;

    @KafkaListener(
            topics = "gigachat.message",
            groupId = "${spring.kafka.groups.request-data}",
            containerFactory = "kafkaGigaChatListenerContainerFactory"
    )
    public void consumerMessageInfo(@Payload ConsumerRecord<String, GigaChatRequestData> consumerData,
                                    @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                                    @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                    @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                    @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) {
        log.info("[KAFKA INFO] Received GigaChatMessageInfo: {}", consumerData.value());
        log.info("[KAFKA INFO] Received Headers: key={}, partition={}, topic={}, ts={}",
                key, partition, topic, ts);

        GigaChatRequestData data = consumerData.value();
        if(nonNull(data)){
           gigaChatService.saveChatInfo(
                   data.content(),
                   null,
                   data.model(),
                   data.userRequestId(),
                   data.userMessage(),
                   null
           );
        }
    }
}
