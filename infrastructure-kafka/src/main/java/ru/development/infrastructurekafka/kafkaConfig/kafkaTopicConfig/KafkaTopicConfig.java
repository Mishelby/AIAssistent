package ru.development.infrastructurekafka.kafkaConfig.kafkaTopicConfig;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import ru.development.infrastructurekafka.enums.KafkaTopic;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaTopicConfig {
    private final KafkaAdmin kafkaAdmin;

    @Bean
    public List<NewTopic> createTopics() {
        return Arrays.stream(KafkaTopic.values())
                .map(newTopic -> TopicBuilder.name(newTopic.name())
                        .partitions(newTopic.getPartition())
                        .replicas(newTopic.getReplication())
                        .build()
                ).toList();
    }
}
