package ru.development.api.telegramApi;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConfigurationProperties(prefix = "telegram")
@Getter
@Setter
public class ApiKeyPrefix {
    private @NonNull String apiKey;
}
