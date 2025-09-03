package ru.development.api.telegramApi;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@FunctionalInterface
public interface TelegramExecutor<T> {
    void accept(T method) throws TelegramApiException;
}
