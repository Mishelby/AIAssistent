package ru.development.infrastructurekafka.model;

import lombok.Builder;

import java.util.List;

@Builder
public record GigaChatProducerInfo(
        String key,
        String sessionId,
        String userRequestId,
        List<String> metadata
) {
}
