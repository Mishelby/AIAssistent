package ru.development.api.telegramApi.telegramConsumer;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.development.api.model.CreateUserRequest;
import ru.development.api.model.UserDto;
import ru.development.api.repository.UserEntityRepository;
import ru.development.api.service.UserService;
import ru.development.api.telegramApi.TelegramBotMainMenuService;
import ru.development.api.telegramApi.model.ChatInfoDto;

import java.util.ArrayList;
import java.util.List;
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
            Нажми старт что бы начать!
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
            ChatInfoDto chatInfo = getChatInfo(update);

            if (!userRepository.existsByUsername(chatInfo.user().getUserName())
                    && "/start".equalsIgnoreCase(chatInfo.text())) {
                keyboardStart(chatInfo.chatId(), WELCOME_MESSAGE);
                createNewUser(chatInfo.user(), chatInfo.chatId());
            }

            if ("Старт!".equals(chatInfo.text())) {
                telegramBotMainMenuService.sendMainMenu(telegramClient, chatInfo.chatId());
            }
        }

            if (update.hasCallbackQuery()) {
            ChatInfoDto chatInfo = getCallBackInfo(update.getCallbackQuery());

            switch (chatInfo.callbackQuery().getData()) {
                case "KNOW_LEVEL" -> telegramBotMainMenuService.chooseYourProgrammingLevel(
                        telegramClient, chatInfo.chatId()
                );

                case "HELP" -> sendHelpFile(telegramClient, chatInfo.chatId());

                default -> sendMessage(chatInfo.chatId(), DEFAULT_MESSAGE);

            }

        }
    }

    private static @NotNull ChatInfoDto getCallBackInfo(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        var userFrom = callbackQuery.getFrom();

        return ChatInfoDto.builder()
                .chatId(chatId)
                .user(userFrom)
                .callbackQuery(callbackQuery)
                .build();
    }

    private static @NotNull ChatInfoDto getChatInfo(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        var userFrom = update.getMessage().getFrom();

        return ChatInfoDto.builder()
                .chatId(chatId)
                .text(text)
                .user(userFrom)
                .build();
    }

    private void createNewUser(User userFrom, Long chatId) {
        saveNewUser((newUserName, userChatId) ->
                        log.info("[TELEGRAM INFO] Новый пользователь добавлен в базу данных: {}, {}",
                                newUserName, userChatId),
                chatId, userFrom);
    }

    private SendMessage sendMessage(Long chatId, String message) {
        SendMessage welcomeMessage = executeMessage(
                () -> SendMessage.builder()
                        .text(message)
                        .chatId(chatId)
                        .build());

        doExecute(telegramClient::execute, welcomeMessage);
        logInfo((newMessage, userChatId) ->
                        log.info("[TELEGRAM INFO] Сообщение: {}, было отправлено в чат: {}", newMessage, userChatId),
                DEFAULT_MESSAGE, chatId);

        return welcomeMessage;
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

    public void logInfo(ObjLongConsumer<String> consumer, String message, Long chatId) {
        consumer.accept(message, chatId);
    }

    public void saveNewUser(
            ObjLongConsumer<String> loggerInfo,
            Long chatId,
            User userFrom
    ) {
        UserDto user = userService.createUser(
                CreateUserRequest.builder()
                        .userName(userFrom.getUserName())
                        .firstName(userFrom.getFirstName())
                        .lastName(userFrom.getLastName())
                        .chatNumber(chatId.toString())
                        .build());

        loggerInfo.accept(user.getUserName(), chatId);
    }

    public void keyboardStart(Long chatId, String message) {
        var startButton = new KeyboardButton("Старт!");
        var keyboardButtons = new KeyboardRow(startButton);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(keyboardButtons);

        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder()
                .keyboard(keyboardRows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();

        var sendMessage = executeMessage(() -> SendMessage.builder()
                .text(message)
                .chatId(chatId)
                .replyMarkup(keyboardMarkup)
                .build());

        doExecute(telegramClient::execute, sendMessage);
    }


    @AfterBotRegistration
    public void afterBotRegistration(BotSession botSession) {
        log.info("[TELEGRAM INFO] Registered bot running state is : {}", botSession);

    }

}
