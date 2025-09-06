package ru.development.api.telegramApi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
@Service
public class TelegramBotMainMenuService {
    private static final String USER_NAME_MESSAGE = " %s\n %s";

    private static final String CHOOSE_YOUR_LEVEL_MESSAGE = """
            Для начала давай определимся какой у тебя уровень?.
            Если ты не знаешь, как это сделать, жми кнопку и я пришлю тебе шпаргалку!
            """;

    private static final String CHOOSE_LEVEL_MESSAGE = """
            Отлично! Просто выбери тот вариант, который как ты считаешь подходит тебе!
            """;

    public void sendMainMenu(TelegramClient telegramClient, Long chatId) {
        SendMessage message = executeMessage(() ->
                SendMessage.builder()
                        .text(CHOOSE_YOUR_LEVEL_MESSAGE)
                        .chatId(chatId)
                        .build()
        );

        var iKnowMyLevel = InlineKeyboardButton.builder()
                .text("Я знаю свой текущий уровень!")
                .callbackData("KNOW_LEVEL")
                .build();

        var needHelp = InlineKeyboardButton.builder()
                .text("Пришли шпаргалку!")
                .callbackData("HELP")
                .build();

        List<InlineKeyboardRow> buttons = List.of(
                new InlineKeyboardRow(iKnowMyLevel),
                new InlineKeyboardRow(needHelp)
        );

        var inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(buttons)
                .build();

        message.setReplyMarkup(inlineKeyboardMarkup);
        doExecute(telegramClient::execute, message);
    }

    public void chooseYourProgrammingLevel(TelegramClient telegramClient, Long chatId) {
        SendMessage message = executeMessage(() -> SendMessage.builder()
                .text(CHOOSE_LEVEL_MESSAGE)
                .chatId(chatId)
                .build());

        var beginner = InlineKeyboardButton.builder()
                .text("Я начинающий программист")
                .callbackData("BEGINNER")
                .build();

        var middle = InlineKeyboardButton.builder()
                .text("У меня средний уровень")
                .callbackData("MIDDLE")
                .build();

        List<InlineKeyboardRow> buttons = List.of(
                new InlineKeyboardRow(beginner),
                new InlineKeyboardRow(middle)
        );

        var inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(buttons)
                .build();

        message.setReplyMarkup(inlineKeyboardMarkup);
        doExecute(telegramClient::execute, message);
    }
}
