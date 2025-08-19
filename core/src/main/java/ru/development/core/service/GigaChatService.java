package ru.development.core.service;

import chat.giga.client.GigaChatClient;
import chat.giga.client.GigaChatClientImpl;
import chat.giga.client.auth.AuthClient;
import chat.giga.model.ModelName;
import chat.giga.model.completion.*;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.development.core.mapper.GigaChatModelInfoMapper;
import ru.development.core.model.GigaChatModelInfo;
import ru.development.core.model.dto.GigaChatModelInfoDto;
import ru.development.core.model.dto.GigaChatResponseDto;
import ru.development.core.repository.GigaChatModelInfoRepository;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;


/**
 * По идее, всё нужно переместить в один сервис, где я передаю чату сообщения и отправляю запросы в другие сервисы
 * и где сохраняю данные в БД (Пока вынес, потому что транзакционный метод не будет работать в том же классе)
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class GigaChatService {
    private final GigaChatModelInfoRepository gigaChatModelInfoRepository;
    private final GigaChatModelInfoMapper gigaChatModelInfoMapper;

    private static final int CONNECT_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 60;

    @Transactional
    @SystemMessage(value = "Ты учитель математики и физики")
    @UserMessage(value = "Привет пользователь!")
    protected GigaChatModelInfoDto sendGigaChatMessage(
            String message,
            String bearerToken,
            String finalUserRequestId
    ) {
        var client = getGigaChatClient(bearerToken);
        var response = getCompletions(message, client, ModelName.GIGA_CHAT, ChatMessageRole.USER);

        List<Choice> choices = nonNull(response.choices()) ? response.choices() : Collections.emptyList();
        GigaChatModelInfo modelInfo = saveChatInfo(choices, response, finalUserRequestId, message, null);

        return getBuild(modelInfo);
    }

    private static GigaChatClientImpl getGigaChatClient(String bearerToken) {
        return GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withProvidedTokenAuth(bearerToken)
                        .build())
                .connectTimeout(CONNECT_TIMEOUT)
                .readTimeout(READ_TIMEOUT)
                .build();
    }

    private static CompletionResponse getCompletions(String message,
                                                     GigaChatClientImpl client,
                                                     String modelName,
                                                     ChatMessageRole chatMessageRole) {
        return client.completions(CompletionRequest.builder()
                .model(modelName)
                .message(ChatMessage.builder()
                        .content(message)
                        .role(chatMessageRole)
                        .build())
                .build());
    }

    private static GigaChatModelInfoDto getBuild(GigaChatModelInfo modelInfo) {
        return GigaChatModelInfoDto.builder()
                .modelName(modelInfo.modelName())
                .message(modelInfo.message())
                .content(modelInfo.content())
                .userRequestId(modelInfo.userRequestId())
                .build();
    }

    protected GigaChatModelInfo saveChatInfo(
            List<Choice> choices,
            CompletionResponse completions,
            String userRequestId,
            String userMessage,
            String status
    ) {
        ChoiceMessage choice = choices.getFirst().message();
        GigaChatModelInfo entity = gigaChatModelInfoMapper.toEntity(
                completions.model(),
                choice.content(),
                choice.role().name(),
                userRequestId,
                userMessage,
                status
        );

        return gigaChatModelInfoRepository.save(entity);

    }

    private static GigaChatResponseDto getGigaChatResponseDto(GigaChatModelInfo modelInfo) {
        return GigaChatResponseDto.builder()
                .message(modelInfo.message())
                .modelName(modelInfo.modelName())
                .gigaChatId(modelInfo.id())
                .build();
    }
}
