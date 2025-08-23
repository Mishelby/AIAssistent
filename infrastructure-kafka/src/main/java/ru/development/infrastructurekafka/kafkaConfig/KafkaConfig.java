package ru.development.infrastructurekafka.kafkaConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.development.infrastructurekafka.model.CheckTokenRequest;
import ru.development.infrastructurekafka.model.GigaChatProducerInfo;
import ru.development.infrastructurekafka.model.GigaChatRequestData;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Конфигурация для GigaChatInfo
     */

    @Bean
    public ProducerFactory<String, GigaChatProducerInfo> producerChatInfoFactory(
            final ObjectMapper objectMapper
    ) {
        final Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        JsonSerializer<GigaChatProducerInfo> jsonSerializer = new JsonSerializer<>(objectMapper);
        jsonSerializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                jsonSerializer
        );
    }

    @Bean
    public KafkaTemplate<String, GigaChatProducerInfo> kafkaChatInfoTemplate(
            final ProducerFactory<String, GigaChatProducerInfo> producerChatInfoFactory
    ) {
        return new KafkaTemplate<>(producerChatInfoFactory);
    }

    @Bean
    public ConsumerFactory<String, GigaChatProducerInfo> consumerChatInfoFactoryFactory(
            final ObjectMapper objectMapper
    ) {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "gigachat-info-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        JsonDeserializer<GigaChatProducerInfo> jsonDeserializer
                = new JsonDeserializer<>(GigaChatProducerInfo.class, objectMapper);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GigaChatProducerInfo> kafkaChatInfoListenerContainerFactory(
            final ConsumerFactory<String, GigaChatProducerInfo> consumerChatInfoFactoryFactory
    ) {
        final ConcurrentKafkaListenerContainerFactory<String, GigaChatProducerInfo> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerChatInfoFactoryFactory);
        return factory;
    }

    /**
     * Конфигурация для GigaChatMessage
     */

    @Bean
    public ProducerFactory<String, GigaChatRequestData> producerGigaChatFactory(
            final ObjectMapper objectMapper
    ) {
        final Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        JsonSerializer<GigaChatRequestData> jsonSerializer = new JsonSerializer<>(objectMapper);
        jsonSerializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                jsonSerializer
        );
    }

    @Bean
    public KafkaTemplate<String, GigaChatRequestData> kafkaGigaChatTemplate(
            final ProducerFactory<String, GigaChatRequestData> producerGigaChatFactory
    ) {
        return new KafkaTemplate<>(producerGigaChatFactory);
    }

    @Bean
    public ConsumerFactory<String, GigaChatRequestData> consumerGigaChatFactoryFactory(
            final ObjectMapper objectMapper
    ) {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "gigachat-request-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        JsonDeserializer<GigaChatRequestData> jsonDeserializer = new JsonDeserializer<>(GigaChatRequestData.class, objectMapper);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GigaChatRequestData> kafkaGigaChatListenerContainerFactory(
            final ConsumerFactory<String, GigaChatRequestData> consumerGigaChatFactoryFactory
    ) {
        final ConcurrentKafkaListenerContainerFactory<String, GigaChatRequestData> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerGigaChatFactoryFactory);
        return factory;
    }

    @Bean
    public ProducerFactory<String, CheckTokenRequest> checkTokenProducerFactory(
            final ObjectMapper objectMapper
    ) {
        final Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        JsonSerializer<CheckTokenRequest> jsonSerializer = new JsonSerializer<>(objectMapper);
        jsonSerializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                jsonSerializer
        );
    }

    @Bean
    public KafkaTemplate<String, CheckTokenRequest> checkTokenKafkaTemplate(
            final ProducerFactory<String, CheckTokenRequest> producerGigaChatFactory
    ) {
        return new KafkaTemplate<>(producerGigaChatFactory);
    }

    @Bean
    public ConsumerFactory<String, CheckTokenRequest> checkTokenConsumerFactoryFactory(
            final ObjectMapper objectMapper
    ) {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "check-token-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        JsonDeserializer<CheckTokenRequest> jsonDeserializer = new JsonDeserializer<>(CheckTokenRequest.class, objectMapper);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CheckTokenRequest> checkTokenKafkaListenerContainerFactory(
            final ConsumerFactory<String, CheckTokenRequest> consumerGigaChatFactoryFactory
    ) {
        final ConcurrentKafkaListenerContainerFactory<String, CheckTokenRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerGigaChatFactoryFactory);
        return factory;
    }

    @Bean
    public ReplyingKafkaTemplate<String, CheckTokenRequest, GigaChatRequestData> replyingKafkaTemplate(
            ProducerFactory<String, CheckTokenRequest> pf,
            ConcurrentKafkaListenerContainerFactory<String, GigaChatRequestData> factory
    ) {
        ConcurrentMessageListenerContainer<String, GigaChatRequestData> repliesContainer =
                factory.createContainer("token.response");
        repliesContainer.getContainerProperties().setGroupId("token-response-group");

        return new ReplyingKafkaTemplate<>(pf, repliesContainer);
    }

}
