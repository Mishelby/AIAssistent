package ru.development.api.telegramApi.service;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.development.api.telegramApi.TelegramExecutor;

import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@UtilityClass
public class ExecuteService {
    private static final String HELP_DOCUMENT_NAME = "Help document";

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
            log.info("[TELEGRAM INFO] Попытка создать и отправить сообщение");
            var sendMessage = supplier.get();
            log.info("[TELEGRAM INFO] Сообщение создано и отправлено: {}, {}",
                    sendMessage.getChatId(), sendMessage.getText());
            return sendMessage;
        } catch (Exception e) {
            log.error("[TELEGRAM ERROR] Ошибка отправки java library! {}", e.getMessage());
            throw new RuntimeException("Ошибка отправки java library!");
        }
    }

    public static SendDocument executeDocument(Supplier<SendDocument> supplier) {
        try {
            var sendDocument = supplier.get();
            log.info("[TELEGRAM INFO] Документ отправлен пользователю!: {}, {}",
                    HELP_DOCUMENT_NAME, sendDocument.getChatId());
            return sendDocument;
        } catch (Exception e){
            log.error("[TELEGRAM ERROR] Ошибка отправки документа! {}", HELP_DOCUMENT_NAME);
            throw new RuntimeException("Ошибка отправки документа! %s"
                    .formatted(HELP_DOCUMENT_NAME), e.getCause());
        }
    }

    public static <T> T genericExecuteMessage(Supplier<T> genericSupplier) {
        return genericSupplier.get();
    }
}
