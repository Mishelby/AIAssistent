package ru.development.main.model.enums;

public enum GigaChatResponseStatus {
    SUCCESS("success"),
    EMPTY_RESPONSE("emptyResponse"),
    FAILURE("failure");

    String description;

    GigaChatResponseStatus(String description) {
        this.description = description;
    }
}
