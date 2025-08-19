package ru.development.core.service;


import chat.giga.http.client.HttpClientException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.development.core.aop.TrackExecutionTime;
import ru.development.core.httpCore.httpClient.IHttpCoreImpl;
import ru.development.core.model.*;
import org.springframework.http.HttpHeaders;
import ru.development.core.model.dto.GigaChatModelInfoDto;
import ru.development.core.model.dto.GigaChatResponse;
import ru.development.infrastructurekafka.model.CheckTokenRequest;
import ru.development.infrastructurekafka.model.GigaChatProducerInfo;
import ru.development.infrastructurekafka.model.GigaChatRequestData;
import ru.development.infrastructurekafka.service.GigachatProducer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Почти везде пока пробрасываю RuntimeException, потом поменяю на кастомные + нормальные
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class GigaChatMessageService {
    // TODO Пока вместо кеша
    private static final Map<String, AccessToken> accessTokens = new HashMap<>();

    private final IHttpCoreImpl httpCore;
    private final ExecutorService executorService;
    private final GigaChatService gigaChatService;
    private final GigachatProducer gigachatProducer;

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
         * Здесь использовал CompletableFuture что бы посмотреть как это работает и когда лучше использовать
         * Здесь есть запрос по url = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth", и запрос в сам гига чат
         * поэтому поставил асинхронны вызов, посмотреть время выполнения (Потом сделаю так, что бы запросы отправлялись
         * через producer)
         **/
        try {
            CompletableFuture<GigaChatModelInfoDto> chatFuture = CompletableFuture.supplyAsync(() ->
                            checkAccessToken(servletRequest), executorService)
                    .thenCompose(token -> {

                        Map<String, String> contextMap = MDC.getCopyOfContextMap();

                        return CompletableFuture.supplyAsync(() -> {
                            if (nonNull(contextMap)) {
                                MDC.setContextMap(contextMap);
                            }
                            MDC.put("ID запроса пользователя: ", finalUserRequestId);
                            MDC.put("Токен доступа: ", token.getAccessToken());


//                            gigachatProducer.sendMainMessage(builder.build());

                            return gigaChatService.sendGigaChatMessage(
                                    correctMessage,
                                    token.getAccessToken(),
                                    finalUserRequestId
                            );
                        }, executorService);
                    }).exceptionally(ex -> {
                        log.error("[ERROR] Ошибка при получении токена или отправке сообщения: {}", ex.getMessage());
                        throw new RuntimeException(ex.getMessage(), ex);
                    });

            CompletableFuture.runAsync(() -> {
                GigaChatProducerInfo gigaChatProducerInfo = getGigaChatProducerInfo(servletRequest, finalUserRequestId);
                gigachatProducer.sendInfoMessage(gigaChatProducerInfo);
            }, executorService).exceptionally(ex -> {
                log.error("[ERROR] Ошибка отправки данных ProducerFactory: {}", ex.getMessage());
                throw new RuntimeException(ex.getMessage(), ex);
            });

            return chatFuture.join();
        } catch (HttpClientException ex) {
            log.error("[ERROR] Ошибка! Не удалось выполнить запрос: {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            MDC.clear();
        }
    }

    // Проверяю, есть ли токен в кеше, если нет, отправляю запрос на получение нового
    public @NonNull AccessToken checkAccessToken(final HttpServletRequest servletRequest) {
        String remoteAddr = servletRequest.getRemoteAddr();

        if (accessTokens.containsKey(remoteAddr) && nonNull(remoteAddr)) {
            AccessToken accessToken = accessTokens.get(remoteAddr);
            long millis = System.currentTimeMillis();

            if (accessToken.getExpiresAt() < millis) {
                log.info("[INFO] Токен будет действовать ещё: {}", ((millis - accessToken.getExpiresAt()) * 60));
                return accessToken;
            } else {
                accessTokens.remove(remoteAddr);
                return getAccessToken(remoteAddr);
            }
        } else {
            return getAccessToken(remoteAddr);
        }
    }

    // TODO переделать без аннотации @NonNull (пробросить ошибку)
    private @NonNull AccessToken getAccessToken(String remoteAddr) {
        MultiValueMap<String, String> formData = getMultiValueMap();
        log.info("[INFO] MultiMap {}", formData);
        try {
            ResponseEntity<AccessToken> responseEntity = getAccessTokenResponseEntity(formData);
            if (nonNull(responseEntity) && nonNull(responseEntity.getBody())) {
                accessTokens.put(remoteAddr, responseEntity.getBody());
                return responseEntity.getBody();
            } else {
                // ! Хз, надо ли? Можно просто пробросить ошибку и не ставить @NonNull !
                return new AccessToken("Incorrect token", 0, 0);
            }
        } catch (HttpClientException ex) {
            log.error("[ERROR] Ошибка! Не удалось выполнить запрос: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private ResponseEntity<AccessToken> getAccessTokenResponseEntity(MultiValueMap<String, String> formData) {
        return httpCore.post(
                "https://ngw.devices.sberbank.ru:9443/api/v2/oauth",
                getHeaders(),
                formDataToString(formData),
                AccessToken.class
        );
    }

    // Здесь захардкодил ключ аутентификации, что бы сразу отправить его в заголовках и получить токен доступа
    public HttpHeaders getHeaders() {
        String clientSecret = "83646a9d-dece-4573-9c57-87e090762966";
        String clientID = "2b16995d-7f48-4823-9fac-21cf15ae08cb";
        String authKey = Base64.getEncoder().encodeToString((clientID + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
        log.info("[INFO] Ключ аутентификации: {}", authKey);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + authKey);
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        httpHeaders.add("Accept", "application/json");
        httpHeaders.add("RqUID", UUID.randomUUID().toString());
        log.info("[INFO] HttpHeaders= {}", httpHeaders);
        return httpHeaders;
    }

    // TODO Пока набросок, переделаю
    private String isMessageCorrect(final String message) {
        String newMessage = null;
        if (message.length() > MAX_MESSAGE_LENGTH) {
            log.info("[INFO] Превышена допустимая длинна сообщения: {}", message);
            int exceededLength = message.length() - MAX_MESSAGE_LENGTH;
            log.info("[INFO] Превышенный лимит: {}, вырезанный контекст {}", exceededLength,
                    message.substring(MAX_MESSAGE_LENGTH, exceededLength));
            newMessage = message.substring(0, MAX_MESSAGE_LENGTH);
        }

        return newMessage;
    }

    // Формирование информации для Producer (Пока просто указал какие-то базовые данные) :)
    private static GigaChatProducerInfo getGigaChatProducerInfo(
            final HttpServletRequest servletRequest,
            final String finalUserRequestId) {
        return GigaChatProducerInfo.builder()
                .key(UUID.randomUUID().toString())
                .userRequestId(finalUserRequestId)
                .sessionId(servletRequest.getSession().getId())
                .metadata(List.of(String.format(Thread.currentThread().getName(), getHeadersNameFromRequest(servletRequest.getHeaderNames()))))
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


    /**
     * Эти три метода нужны для того, что бы отправить scope в теле запроса, т.к я сделал надстройку над Http клиентом
     *
     * @param formData мультимапа для формирования скоупа
     * @return String
     */
    private String formDataToString(MultiValueMap<String, String> formData) {
        return formData.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(value -> encode(entry.getKey()) + "=" + encode(value)))
                .collect(Collectors.joining("&"));
    }

    private static MultiValueMap<String, String> getMultiValueMap() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("scope", "GIGACHAT_API_PERS");
        return formData;
    }

    private String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

}
