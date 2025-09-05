package ru.development.api.telegramApi.telegramConsumer;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.development.api.telegramApi.service.ExecuteService;

import java.util.List;

import static ru.development.api.telegramApi.service.ExecuteService.executeMessage;

@Slf4j
@UtilityClass
public final class JavaLibraryService {
    private static final String USER_NAME_MESSAGE = " %s %s";

    public static void chooseJavaLanguageLevel(TelegramClient telegramClient, Long chatId, User user) {
        SendMessage message = executeMessage(() ->
                SendMessage.builder()
                        .text(USER_NAME_MESSAGE.formatted(user.getFirstName(),
                                " Выбери свой текущий уровень"))
                        .chatId(chatId)
                        .build()
        );

        var beginner = InlineKeyboardButton.builder()
                .text("Начинающий")
                .callbackData("beginner")
                .build();

        var middle = InlineKeyboardButton.builder()
                .text("Средний")
                .callbackData("middle")
                .build();

        var senior = InlineKeyboardButton.builder()
                .text("Продвинутый")
                .callbackData("senior")
                .build();

        List<InlineKeyboardRow> buttons = List.of(
                new InlineKeyboardRow(beginner),
                new InlineKeyboardRow(middle),
                new InlineKeyboardRow(senior)
        );

        var inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(buttons)
                .build();

        message.setReplyMarkup(inlineKeyboardMarkup);
        ExecuteService.doExecute(telegramClient::execute, message);
    }
}
