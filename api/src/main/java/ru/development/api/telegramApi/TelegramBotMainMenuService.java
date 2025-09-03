package ru.development.api.telegramApi;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import java.util.List;

@Slf4j
@Service
public class TelegramBotMainMenuService {

    @SneakyThrows
    protected void sendMainMenu(TelegramClient telegramClient, Long chatId) {
        SendMessage message = SendMessage.builder()
                .text("Привет! Выбери тему")
                .chatId(chatId)
                .build();

        var javaButton = InlineKeyboardButton.builder()
                .text("Библиотека java")
                .callbackData("java")
                .build();

        var sqlButton = InlineKeyboardButton.builder()
                .text("Библиотека SQL")
                .callbackData("sql")
                .build();

        var specialButton = InlineKeyboardButton.builder()
                .text("Задать вопрос специалисту")
                .callbackData("special")
                .build();

        List<InlineKeyboardRow> buttons = List.of(
                new InlineKeyboardRow(javaButton),
                new InlineKeyboardRow(sqlButton),
                new InlineKeyboardRow(specialButton)
        );

        InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(buttons)
                .build();

        message.setReplyMarkup(inlineKeyboardMarkup);

        telegramClient.execute(message);
    }
}
