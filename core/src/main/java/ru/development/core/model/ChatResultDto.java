package ru.development.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class ChatResultDto implements GigaChatResponse {
    private final GigaChatResponse response;
    private final Throwable error;

    @Override
    public String getModelName() {
        return response.getModelName();
    }

    @Override
    public Long getGigaChatId() {
        return response.getGigaChatId();
    }

    @Override
    public String getMessage() {
        return response.getMessage();
    }

    @Override
    public String getType() {
        return response.getType();
    }
}
