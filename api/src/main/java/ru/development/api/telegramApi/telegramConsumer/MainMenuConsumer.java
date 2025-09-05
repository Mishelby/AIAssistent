package ru.development.api.telegramApi.telegramConsumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.development.api.model.CreateUserRequest;
import ru.development.api.repository.UserEntityRepository;
import ru.development.api.service.UserService;
import ru.development.api.telegramApi.TelegramBotMainMenuService;

import java.util.concurrent.CompletableFuture;
import java.util.function.ObjLongConsumer;

import static java.util.Objects.nonNull;
import static ru.development.api.telegramApi.TelegramBotJavaService.sendJavaLibrary;
import static ru.development.api.telegramApi.service.ExecuteService.*;
import static ru.development.api.telegramApi.service.JavaLibraryService.chooseJavaLanguageLevel;
import static ru.development.api.telegramApi.service.JavaLibraryService.sendHelpFile;


@Slf4j
@Component
public class MainMenuConsumer implements LongPollingSingleThreadUpdateConsumer {
    private final UserService userService;
    private final UserEntityRepository userRepository;
    private static final String DEFAULT_MESSAGE = "Я вас не понимаю";
    private static final String WELCOME_MESSAGE = """
            Привет!
            Этот бот предназначен для изучения материала по различным темам языка программирования Java!
            В нём ты будешь получать весь необходимый теоретический материал а так же домашние задания.
            Вся информация разделена по уровням, от начинающего до более продвинутого.
            Доступ к более высоким уровням будет открываться после выполнения всех домашних работ твоего текущего
            уровня.
            """;
    private static final String WAITING_MESSAGE = "Отлично! Но для начала укажи свой уровень";
    private final TelegramClient telegramClient;
    private final TelegramBotMainMenuService telegramBotMainMenuService;

    public MainMenuConsumer(
            UserService userService, UserEntityRepository userRepository,
            TelegramClient telegramClient,
            TelegramBotMainMenuService telegramBotMainMenuService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.telegramClient = telegramClient;
        this.telegramBotMainMenuService = telegramBotMainMenuService;
    }

    @Override
    public void consume(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            var userFrom = update.getMessage().getFrom();
            if (!userRepository.existsByUsername(userFrom.getUserName())) {
                sendMessage(chatId, WELCOME_MESSAGE);
                createNewUser(userFrom.getUserName(), userFrom, chatId);
            }
            var user = update.getMessage().getFrom();
            if ("/start".equals(text)) {
                telegramBotMainMenuService.sendMainMenu(telegramClient, chatId, user);
            }
        }

        if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            var callbackQuery = update.getCallbackQuery();
            var user = callbackQuery.getFrom();
            String callbackQueryData = callbackQuery.getData();

            switch (callbackQueryData) {
                case "know_level" -> {
                    telegramBotMainMenuService.chooseYourProgrammingLevel(telegramClient, chatId, user);
                    return;
                }
                case "help" -> {
                    sendHelpFile(telegramClient, chatId);
                    return;
                }
                default -> {
                    sendMessage(chatId, DEFAULT_MESSAGE);
                    return;
                }
            }

        }

        if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            var callbackQuery = update.getCallbackQuery();
            var user = callbackQuery.getFrom();
            String callbackQueryData = callbackQuery.getData();

            switch (callbackQueryData) {
                case "java" -> doWork(chatId, user);
                case "sql" -> sendMessage(chatId, DEFAULT_MESSAGE);
                case "special" -> sendMessage(chatId, DEFAULT_MESSAGE);
                default -> sendMessage(chatId, DEFAULT_MESSAGE);
            }
        }

    }

    private void createNewUser(String userName, User userFrom, Long chatId) {
        userService.createUser(CreateUserRequest.builder()
                .userName(userName)
                .firstName(userFrom.getFirstName())
                .lastName(userFrom.getLastName())
                .chatNumber(chatId.toString())
                .build());
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage welcomeMessage = executeMessage(
                () -> SendMessage.builder()
                        .text(message)
                        .chatId(chatId)
                        .build());

        doExecute(telegramClient::execute, welcomeMessage);
        logInfo((String str, long chat) -> {
            log.info("[TELEGRAM INFO] Сообщение: {}, было отправлено в чат: {}", str, chat);
        }, DEFAULT_MESSAGE, chatId);
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
