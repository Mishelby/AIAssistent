package ru.development.api.telegramApi;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;


@Slf4j
@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final TelegramBotJavaService telegramBotJavaService;
    private final TelegramBotMainMenuService telegramBotMainMenuService;

    public UpdateConsumer(@Autowired ApiKeyPrefix apiKey,
                          TelegramBotJavaService telegramBotJavaService,
                          TelegramBotMainMenuService telegramBotMainMenuService) {
        telegramClient = new OkHttpTelegramClient(apiKey.getApiKey());
        this.telegramBotJavaService = telegramBotJavaService;
        this.telegramBotMainMenuService = telegramBotMainMenuService;
    }

    @SneakyThrows
    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (text.equals("/start")) {
                telegramBotMainMenuService.sendMainMenu(telegramClient, chatId);
            } else {
                defaultMessage(chatId, "Я вас не понимаю...");
            }
        } else if (update.hasCallbackQuery()) {
            handleCallBackQuery(update.getCallbackQuery());
        }
    }

    @SneakyThrows
    private void defaultMessage(Long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .text(message)
                .chatId(chatId)
                .build();

        telegramClient.execute(sendMessage);
    }

    @SneakyThrows
    private void special(Long chatId, User user) {
        SendMessage sendMessage = SendMessage.builder()
                .text(user.getFirstName() + " вы можете задать интересующий вопрос нашему специалисту!")
                .chatId(chatId)
                .build();

        telegramClient.execute(sendMessage);
    }

    private void sqlMessage(Long chatId, User user) {

    }

    @SneakyThrows
    private void javaMessage(Long chatId, User user) {
        telegramBotJavaService.sendJavaLibrary(telegramClient, chatId, user);
    }


    public void handleCallBackQuery(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();
        var chatId = callbackQuery.getFrom().getId();
        var user = callbackQuery.getFrom();

        switch (data) {
            case "java" -> javaMessage(chatId, user);
            case "sql" -> sqlMessage(chatId, user);
            case "special" -> special(chatId, user);
            default -> defaultMessage(chatId, "Неизвестная команда!");
        }
    }

}
