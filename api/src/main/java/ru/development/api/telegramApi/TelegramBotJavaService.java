package ru.development.api.telegramApi;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Slf4j
@Service
public class TelegramBotJavaService {

    @SneakyThrows
    protected void sendJavaLibrary(TelegramClient telegramClient, Long chatId, User user){
        var message = SendMessage.builder()
                .text(user.getFirstName() + " Выберете тему, которую хотели бы изучить")
                .chatId(chatId)
                .build();


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
        ExecuteService.doExecute(telegramClient::execute, message);
    }
}
