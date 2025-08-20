package ru.development.main.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
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
                                    ConsumerRecordMetadata meta) {
        log.info("[KAFKA INFO] Received GigaChatMessageInfo: {}", consumerData.value());
        log.info("[KAFKA INFO] ConsumerRecordMetadata: {}", meta);

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
