package ru.development.api.telegramApi.service;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.development.api.telegramApi.TelegramExecutor;

import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@UtilityClass
public class ExecuteService {
    public static <T> void doExecute(TelegramExecutor<T> telegramExecutor, T message) {
        try {
            telegramExecutor.accept(message);
        } catch (TelegramApiException e) {
            log.error("[TELEGRAM ERROR] Telegram Api Exception! {}, {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static <T, R> R genericExecute(Function<T, R> telegramExecutor, T message) {
        return telegramExecutor.apply(message);
    }

    public static SendMessage executeMessage(Supplier<SendMessage> supplier) {
        try {
            var sendMessage = supplier.get();
            log.info("[TELEGRAM INFO] Создано сообщение для чата: {}, {}",
                    sendMessage.getChatId(), sendMessage.getText());
            return sendMessage;
        } catch (Exception e) {
            log.error("[TELEGRAM ERROR] Ошибка отправки java library! {}", e.getMessage());
            throw new RuntimeException("Ошибка отправки java library!");
        }
    }

    public static <T> T genericExecuteMessage(Supplier<T> genericSupplier) {
        return genericSupplier.get();
    }
}
