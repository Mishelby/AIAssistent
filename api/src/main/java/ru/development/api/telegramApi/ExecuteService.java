package ru.development.api.telegramApi;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@UtilityClass
public class ExecuteService {
    public static <T> void doExecute(TelegramExecutor<T> telegramExecutor, T message) {
        try {
            telegramExecutor.accept(message);
        } catch (TelegramApiException e) {
            log.error("[ERROR] Telegram Api Exception! {}, {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
