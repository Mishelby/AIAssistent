package ru.development.api.telegramApi.service;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static ru.development.api.telegramApi.service.ExecuteService.*;

@Slf4j
@UtilityClass
public final class JavaLibraryService {
    private static final String USER_NAME_MESSAGE = " %s %s";
    private static final String HELP_FILE_PATH
            = "C:\\Users\\Lores\\IdeaProjects\\AIAgent\\api\\src\\main\\java\\ru\\development\\api\\files\\help-file.pdf";
    private static final String SEND_HELP_FILE
            = "Отлично! Держи файл который поможет тебе определить твой текущий уровень!";

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
        doExecute(telegramClient::execute, message);
    }

    public static void sendHelpFile(TelegramClient telegramClient, Long chatId) {
        SendMessage message = executeMessage(() -> sendMessage(SEND_HELP_FILE, chatId));
        SendDocument sendDocument = executeDocument(() -> getSendDocument(HELP_FILE_PATH, chatId));

        doExecute(telegramClient::execute, message);
        CompletableFuture.runAsync(() ->
            doExecute(telegramClient::execute, sendDocument)
        );
    }

    private static SendDocument getSendDocument(String path, Long chatId) {
        return SendDocument.builder()
                .document(new InputFile(new File(path)))
                .chatId(chatId)
                .build();
    }

    private static SendMessage sendMessage(String text, Long chatId) {
        return SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .build();
    }
}
