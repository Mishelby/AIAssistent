package ru.development.main.service;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.model.ModelName;
import chat.giga.model.completion.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.apache.hadoop.hdfs.protocol.HdfsConstants.READ_TIMEOUT;
import static ru.development.main.service.GigaChatService.CONNECT_TIMEOUT;

/**
 * Использую mockapi.io для создания api песочницы, где буду храниться документы с определёнными параметрами
 * Буду использовать для обучения AI ассистента
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class FunctionService {

    public static final String DOCUMENT_NAME = "document_name";
    public static final String DOCUMENT_TYPE = "document_type";
    public static final String SERIAL_NUMBER = "serial_number";
    public static final String STRING_TYPE = "string";

    public GigaChatClient getGigaChatClient(String bearerToken) {
        return GigaChatClient.builder()
                .maxRetriesOnAuthError(3)
                .authClient(AuthClient.builder()
                        .withProvidedTokenAuth(bearerToken)
                        .build())
                .connectTimeout(CONNECT_TIMEOUT)
                .readTimeout(READ_TIMEOUT)
                .build();
    }


    public void createFunction(String bearerToken, String content) {
        var gigaChatClient = getGigaChatClient(bearerToken);
        var messages = new ArrayList<ChatMessage>();

        messages.add(ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .content(content)
                .build());

        var function = ChatFunction.builder()
                .name("search_document")
                .description("Получение документов из mockapi.io по переданным параметрам")
                .parameters(ChatFunctionParameters.builder()
                        .type("object")
                        .property(DOCUMENT_NAME, ChatFunctionParametersProperty.builder()
                                .type(STRING_TYPE)
                                .description("Название документа")
                                .build())
                        .property(DOCUMENT_TYPE, ChatFunctionParametersProperty.builder()
                                .type(STRING_TYPE)
                                .description("Тип документа")
                                .build())
                        .property(SERIAL_NUMBER, ChatFunctionParametersProperty.builder()
                                .type(STRING_TYPE)
                                .description("Серийный номер документа (число)")
                                .build())
                        .required(List.of(DOCUMENT_TYPE, SERIAL_NUMBER)).build())
                .fewShotExample(ChatFunctionFewShotExample.builder()
                        .request("Пришли документ с типом application и серийный номером 251588012, срок действия " +
                                "не раньше июня 2025 года")
                        .param(DOCUMENT_NAME, "sympathetically_aw.opus")
                        .param(DOCUMENT_TYPE, "application")
                        .param(SERIAL_NUMBER, "307028316")
                        .param("end_date", "2025-08-21T17:41:55.992Z")
                        .build())
                .returnParameters(ChatFunctionParameters.builder()
                        .type("object")
                        .property(DOCUMENT_NAME, ChatFunctionParametersProperty.builder()
                                .type(STRING_TYPE)
                                .description("Название документа")
                                .build())
                        .property(DOCUMENT_TYPE, ChatFunctionParametersProperty.builder()
                                .type(STRING_TYPE)
                                .description("Тип документа, например 'application'")
                                .build())
                        .property(SERIAL_NUMBER, ChatFunctionParametersProperty.builder()
                                .type(STRING_TYPE)
                                .description("Серийный номер документа")
                                .build())
                        .property("start_date", ChatFunctionParametersProperty.builder()
                                .type(STRING_TYPE)
                                .description("Дата начала действия документа в формате ISO 8601")
                                .build())
                        .property("end_date", ChatFunctionParametersProperty.builder()
                                .type(STRING_TYPE)
                                .description("Дата окончания действия документа в формате ISO 8601")
                                .build())
                        .property("id", ChatFunctionParametersProperty.builder()
                                .type(STRING_TYPE)
                                .description("Уникальный идентификатор документа")
                                .build())
                        .property("error", ChatFunctionParametersProperty.builder()
                                .type(STRING_TYPE)
                                .description("Возвращается при возникновении ошибки. Содержит описание ошибки")
                                .build())
                        .build())
                .build();

        var completionResponse = gigaChatClient.completions(CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT)
                .messages(messages)
                .function(function)
                .build());

        var message = completionResponse.choices()
                .getFirst()
                .message();

    }
}
