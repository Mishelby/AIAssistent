package ru.development.infrastructurekafka.model;

import lombok.Builder;

import java.awt.*;
import java.util.List;

@Builder
public record GigaChatRequestData(
        String content,
        String model,
        String userRequestId,
        String userMessage,
        String status
) implements RequestDataType {
}
