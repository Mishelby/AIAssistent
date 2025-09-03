package ru.development.main;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TestResponse(
        String name,
        String email,
        String password,
        @JsonProperty("created_at")
        String createdAt,
        String id
) {
}
