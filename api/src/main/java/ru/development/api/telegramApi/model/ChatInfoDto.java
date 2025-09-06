package ru.development.api.telegramApi.model;

import lombok.Builder;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

@Builder
public record ChatInfoDto(
        User user,
        String text,
        Long chatId,
        CallbackQuery callbackQuery
) {
}
