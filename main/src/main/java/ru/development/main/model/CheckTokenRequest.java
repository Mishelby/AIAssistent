package ru.development.main.model;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record CheckTokenRequest(
        String remoteAddr,
        String correlationId
) implements Serializable {
}
