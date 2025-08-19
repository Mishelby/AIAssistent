package ru.development.core.model.dto;

import lombok.Builder;

@Builder
public record GigaChatRequestData(
        String message,
        String bearerToken,
        String finalUserRequestId
) implements RequestDataType {
}
