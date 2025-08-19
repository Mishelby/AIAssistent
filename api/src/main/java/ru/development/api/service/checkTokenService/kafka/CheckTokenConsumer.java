//package ru.development.api.service.checkTokenService.kafka;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Service;
//import ru.development.api.model.AccessToken;
//import ru.development.api.model.CheckTokenRequest;
//import ru.development.api.service.checkTokenService.CheckTokenService;
//
//import static java.util.Objects.nonNull;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CheckTokenConsumer {
//    private final CheckTokenService checkTokenService;
//
//    @KafkaListener(
//            topics = "token.info",
//            groupId = "token-checker",
//            containerFactory = "kafkaChatInfoListenerContainerFactory"
//    )
//    public void consumerCheckToken(@Payload ConsumerRecord<String, CheckTokenRequest> consumerData,
//                                   @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
//                                   @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
//                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
//                                   @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts,
//                                   @Header(KafkaHeaders.CORRELATION_ID) String correlationId) {
//        log.info("[KAFKA INFO] Received Check Token Request: {}", consumerData.value());
//        log.info("[KAFKA INFO] Received Headers: key={}, partition={}, topic={}, ts={}, correlationId={}",
//                key, partition, topic, ts, correlationId);
//        CheckTokenRequest request = consumerData.value();
//
//        if(nonNull(request) && nonNull(request.remoteAddr())){
//            AccessToken accessToken = checkTokenService.checkAccessToken(request.remoteAddr());
//            ProducerRecord<String, AccessToken> producerRecord = new ProducerRecord<>(
//                    "chat-info",
//                    key,
//                    accessToken
//            );
//        }
//    }
//}
