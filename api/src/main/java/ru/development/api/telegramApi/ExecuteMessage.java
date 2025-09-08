package ru.development.api.telegramApi;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface ExecuteMessage<T> {
    void execute(T message) throws TelegramApiException;
}
