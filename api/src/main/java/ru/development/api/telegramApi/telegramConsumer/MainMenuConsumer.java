package ru.development.api.telegramApi.telegramConsumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.development.api.telegramApi.ExecuteService;
import ru.development.api.telegramApi.TelegramBotJavaService;
import ru.development.api.telegramApi.TelegramBotMainMenuService;

import java.util.concurrent.CompletableFuture;
import java.util.function.ObjLongConsumer;

import static ru.development.api.telegramApi.ExecuteService.doExecute;
import static ru.development.api.telegramApi.TelegramBotJavaService.executeMessage;
import static ru.development.api.telegramApi.TelegramBotJavaService.sendJavaLibrary;
import static ru.development.api.telegramApi.telegramConsumer.JavaLibraryConsumer.chooseJavaLanguageLevel;


@Slf4j
@Component
public class MainMenuConsumer implements LongPollingSingleThreadUpdateConsumer {
    private static final String DEFAULT_MESSAGE = "Я вас не понимаю";
    private static final String WAITING_MESSAGE = "Отлично! Но для начала укажи свой уровень";
    private final TelegramClient telegramClient;
    private final TelegramBotMainMenuService telegramBotMainMenuService;

    public MainMenuConsumer(TelegramClient telegramClient,
                            TelegramBotMainMenuService telegramBotMainMenuService) {
        this.telegramClient = telegramClient;
        this.telegramBotMainMenuService = telegramBotMainMenuService;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            var user = update.getMessage().getFrom();
            Long chatId = update.getMessage().getChatId();
            if ("/start".equals(text)) {
                telegramBotMainMenuService.sendMainMenu(telegramClient, chatId, user);
            } else {
                defaultMessage(chatId);
            }
        }

        if (update.hasCallbackQuery()) {
            var callbackQuery = update.getCallbackQuery();
            var user = callbackQuery.getFrom();
            Long chatId = callbackQuery.getMessage().getChatId();
            String callbackQueryData = callbackQuery.getData();

            switch (callbackQueryData) {
                case "java" -> doWork(chatId, user);
                case "sql" -> defaultMessage(chatId);
                case "special" -> defaultMessage(chatId);
                default -> defaultMessage(chatId);
            }

        }
    }

    private void doWork(Long chatId, User user) {
        var sendMessage = executeMessage(() -> SendMessage.builder()
                .text(WAITING_MESSAGE)
                .chatId(chatId)
                .build());

        doExecute(telegramClient::execute, sendMessage);

        CompletableFuture.runAsync(() ->
                chooseJavaLanguageLevel(telegramClient, chatId, user)
        );
    }


    private void defaultMessage(Long chatId) {
        SendMessage defaultMessage = executeMessage(() -> SendMessage.builder()
                .text(DEFAULT_MESSAGE)
                .chatId(chatId)
                .build());

        doExecute(telegramClient::execute, defaultMessage);

        logInfo((String str, long chat) -> {
            log.info("[TELEGRAM INFO] Сообщение: {}, было отправлено в чат: {}", str, chat);
        }, DEFAULT_MESSAGE, chatId);
    }

    public void logInfo(ObjLongConsumer<String> loggerInfo, String message, long chatId) {
        loggerInfo.accept(message, chatId);
    }

    private void special(Long chatId, User user) {
        SendMessage message = SendMessage.builder()
                .text(user.getFirstName() + " Ты можешь задать вопрос специалисту!\n" +
                        " Чем подробнее ты опишешь свой запрос, тем точнее получишь ответ!")
                .chatId(chatId)
                .build();

        doExecute(telegramClient::execute, message);
    }

    private void sqlMessage(Long chatId, User user) {

    }

    private void javaMessage(Long chatId, User user) {
        sendJavaLibrary(telegramClient, chatId, user);
    }


}
