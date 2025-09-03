package ru.development.api.telegramApi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

@Component
public class TelegramBot implements SpringLongPollingBot {
    private final UpdateConsumer updateConsumer;
    private final ApiKeyPrefix apiKeyPrefix;

    public TelegramBot(UpdateConsumer updateConsumer, ApiKeyPrefix apiKeyPrefix) {
        this.updateConsumer = updateConsumer;
        this.apiKeyPrefix = apiKeyPrefix;
    }

    @Override
    public String getBotToken() {
        return apiKeyPrefix.getApiKey();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateConsumer;
    }
}
