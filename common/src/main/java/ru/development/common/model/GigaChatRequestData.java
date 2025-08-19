package ru.development.common.model;

import lombok.Builder;

@Builder
public record GigaChatRequestData(
        String message,
        String bearerToken,
        String finalUserRequestId
) implements RequestDataType {
}