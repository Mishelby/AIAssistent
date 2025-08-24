package ru.development.main.service;

import chat.giga.http.client.HttpClientException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.development.main.aop.TrackExecutionTime;
import org.springframework.http.HttpHeaders;
import ru.development.main.model.dto.GigaChatResponse;
import ru.development.infrastructurekafka.model.GigaChatProducerInfo;
import ru.development.infrastructurekafka.model.GigaChatRequestData;
import ru.development.infrastructurekafka.service.GigachatProducer;

import java.util.*;
import java.util.List;
import java.util.concurrent.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Почти везде пока пробрасываю RuntimeException, потом поменяю на кастомные + нормальные
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class GigaChatMessageService {
    private final GigachatProducer gigachatProducer;
    private final CheckTokenService checkTokenService;
    private final ExecutorService executorService;
    private final GigaChatService gigaChatService;
    public static final int MAX_MESSAGE_LENGTH = 5000;

    @TrackExecutionTime
    public @NonNull GigaChatResponse sendMessage(final HttpServletRequest servletRequest,
                                                 HttpHeaders headers,
                                                 String message) {
        final String correctMessage = isMessageCorrect(message);

        String userRequestId = headers.getFirst("userRequestId");
        if (isNull(userRequestId)) {
            log.info("Пустой ID запроса пользователя");
            userRequestId = UUID.randomUUID().toString();
            log.info("Теперь ID запроса пользователя: {}", userRequestId);
        }
        final String finalUserRequestId = userRequestId;

        /**
         * Честно, пока не особо понимаю суть кафки. Т.к у меня почти вся логика выполняется локально, буду добавлять
         * сторонние сервисы, что бы можно было отправлять асинхронные запросы
         **/
        try {
            var accessToken = checkTokenService.checkAccessToken(servletRequest.getRemoteAddr());
            String token = accessToken.getAccessToken();

            var modelInfo = gigaChatService.sendGigaChatMessage(
                    correctMessage,
                    token,
                    finalUserRequestId
            );

            GigaChatRequestData requestData
                    = getGigaChatRequestData(modelInfo.content(), correctMessage, finalUserRequestId);

            /**
             * Отправляю синхронно, т.к мне нужно дождаться результата проверки токена
             * Пробовал делать асинхронную цепочку, было бесконечное ожидание, решил сделать проще
             */
            gigachatProducer.sendMainMessage("gigachat.message", userRequestId, requestData);

            /**
             * Отправляю асинхронно, т.к здесь мне не нужно ждать промежуточных операций и я сразу отправляю данные
             * через продьюсер в кафку
             */
            CompletableFuture.runAsync(() -> {
                GigaChatProducerInfo gigaChatProducerInfo
                        = getGigaChatProducerInfo(servletRequest, finalUserRequestId);
                gigachatProducer.sendInfoMessage(
                        "gigachat.info", servletRequest.getSession().getId(), gigaChatProducerInfo
                );
            }, executorService).exceptionally(ex -> {
                log.error("[ERROR] Ошибка при отправке данных в топик: {}", "gigachat.info", ex);
                return null;
            });

            return modelInfo;
        } catch (HttpClientException ex) {
            log.error("[ERROR] Ошибка! Не удалось выполнить запрос: {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            MDC.clear();
        }
    }

    private static GigaChatRequestData getGigaChatRequestData(String content,
                                                              String message,
                                                              String finalUserRequestId) {
        return GigaChatRequestData
                .builder()
                .userMessage(message)
                .content(content)
                .userRequestId(finalUserRequestId)
                .status(null)
                .build();
    }


    // TODO Пока набросок, переделаю
    private String isMessageCorrect(final String message) {
        String newMessage = null;
        if (message.length() > MAX_MESSAGE_LENGTH) {
            log.info("[INFO] Превышена допустимая длинна сообщения: {}", message);
            int exceededLength = message.length() - MAX_MESSAGE_LENGTH;
            log.info("[INFO] Превышенный лимит: {}, вырезанный контекст {}", exceededLength,
                    message.substring(MAX_MESSAGE_LENGTH));
            newMessage = message.substring(0, MAX_MESSAGE_LENGTH);
        } else {
            return message;
        }

        return newMessage;
    }

    // Формирование информации для Producer (Пока просто указал какие-то базовые данные) :)
    private static GigaChatProducerInfo getGigaChatProducerInfo(
            final HttpServletRequest servletRequest,
            final String finalUserRequestId
    ) {
        String sessionId = nonNull(servletRequest.getSession())
                ? servletRequest.getSession().getId()
                : "UNKNOW SESSION";

        return GigaChatProducerInfo.builder()
                .key(UUID.randomUUID().toString())
                .userRequestId(finalUserRequestId)
                .sessionId(sessionId)
                .metadata(List.of(String.format(Thread.currentThread().getName(),
                        getHeadersNameFromRequest(servletRequest.getHeaderNames()))))
                .build();

    }

    private static List<String> getHeadersNameFromRequest(Enumeration<String> headerNames) {
        if (isNull(headerNames)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        headerNames.asIterator().forEachRemaining(header -> {
            if (nonNull(header)) result.add(header);
        });

        return result;
    }


}
