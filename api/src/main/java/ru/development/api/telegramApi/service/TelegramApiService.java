package ru.development.api.telegramApi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

import static ru.development.api.telegramApi.service.ExecuteService.doExecute;
import static ru.development.api.telegramApi.service.ExecuteService.executeMessage;

@Slf4j
@Service
public final class TelegramApiService {

    private TelegramApiService(){

    }

    public static void mainMenuKeyboard(TelegramClient telegramClient, Long chatId, String message) {
        List<KeyboardRow> keyboardRows1 = getKeyboardRows1();
        List<KeyboardRow> keyboardRows2 = getKeyboardRows2();

        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder()
                .keyboard(keyboardRows1)
                .keyboard(keyboardRows2)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .build();

        var sendMessage = executeMessage(() -> SendMessage.builder()
                .text(message)
                .chatId(chatId)
                .replyMarkup(keyboardMarkup)
                .build());

        doExecute(telegramClient::execute, sendMessage);
    }

    @NotNull
    private static List<KeyboardRow> getKeyboardRows1() {
        var currentClass = new KeyboardButton("Текущая тема");
        var getHoweWork = new KeyboardButton("Получить домашнюю работу");
        var keyboardButtons = new KeyboardRow();

        keyboardButtons.add(currentClass);
        keyboardButtons.add(getHoweWork);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(keyboardButtons);
        return keyboardRows;
    }

    @NotNull
    private static List<KeyboardRow> getKeyboardRows2() {
        var sendHoweWork = new KeyboardButton("Отправить домашнюю работу на проверку");
        var toNextLeve = new KeyboardButton("Перейти на следующий уровень");
        var keyboardButtons = new KeyboardRow();

        keyboardButtons.add(sendHoweWork);
        keyboardButtons.add(toNextLeve);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(keyboardButtons);
        return keyboardRows;
    }
}
