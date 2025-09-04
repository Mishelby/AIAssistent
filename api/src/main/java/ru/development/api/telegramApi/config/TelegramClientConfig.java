package ru.development.api.telegramApi.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.development.api.telegramApi.ApiKeyPrefix;

@Slf4j
@Configuration
public class TelegramClientConfig {
    private final ApiKeyPrefix apiKeyPrefix;

    public TelegramClientConfig(ApiKeyPrefix apiKeyPrefix) {
        this.apiKeyPrefix = apiKeyPrefix;
    }

    @Bean
    public TelegramClient telegramClient(){
        return new OkHttpTelegramClient(apiKeyPrefix.getApiKey());
    }


    @PostConstruct
    public void postLoad(){
        log.info("[TELEGRAM INFO] ApiKeyPrefix: {}", apiKeyPrefix.getApiKey());
    }
}
