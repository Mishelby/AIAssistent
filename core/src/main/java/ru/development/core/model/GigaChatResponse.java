package ru.development.core.model;

public sealed interface GigaChatResponse permits GigaChatResponseDto, ChatResultDto{
    String getModelName();
    Long getGigaChatId();
    String getMessage();
    String getType();
}
