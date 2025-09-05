package ru.development.api.telegramApi;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

import static ru.development.api.telegramApi.service.ExecuteService.doExecute;
import static ru.development.api.telegramApi.service.ExecuteService.executeMessage;

@Slf4j
@UtilityClass
public class TelegramBotJavaService {
    private static final String USER_NAME_MESSAGE = " %s %s";

    public static void sendJavaLibrary(TelegramClient telegramClient, Long chatId, User user) {
        SendMessage message = executeMessage(() ->
                SendMessage.builder()
                        .text(USER_NAME_MESSAGE.formatted(user.getFirstName(),
                                " Выберете раздел, которую хотели бы изучить"))
                        .chatId(chatId)
                        .build()
        );

        var javaCore = InlineKeyboardButton.builder()
                .text("Java core")
                .callbackData("java core")
                .build();

        var spring = InlineKeyboardButton.builder()
                .text("Spring framework")
                .callbackData("spring framework")
                .build();

        List<InlineKeyboardRow> buttons = List.of(
                new InlineKeyboardRow(javaCore),
                new InlineKeyboardRow(spring)
        );

        var inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(buttons)
                .build();

        message.setReplyMarkup(inlineKeyboardMarkup);
        doExecute(telegramClient::execute, message);
    }

}
