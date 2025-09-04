package ru.development.api.telegramApi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import ru.development.api.telegramApi.telegramConsumer.MainMenuConsumer;

@Component
public class TelegramBot implements SpringLongPollingBot {
    private final MainMenuConsumer mainMenuConsumer;
    private final ApiKeyPrefix apiKeyPrefix;

    public TelegramBot(MainMenuConsumer mainMenuConsumer, ApiKeyPrefix apiKeyPrefix) {
        this.mainMenuConsumer = mainMenuConsumer;
        this.apiKeyPrefix = apiKeyPrefix;
    }

    @Override
    public String getBotToken() {
        return apiKeyPrefix.getApiKey();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return mainMenuConsumer;
    }
}
