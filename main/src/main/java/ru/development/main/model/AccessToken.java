package ru.development.main.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.CompletableFuture;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccessToken extends CompletableFuture<AccessToken> {
    @JsonProperty("access_token")
    String accessToken;
    @JsonProperty("expires_at")
    long expiresAt;
    long expiresAfter;
}
