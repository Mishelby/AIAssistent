package ru.development.core.service;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.ModelName;
import chat.giga.model.completion.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.development.core.aop.TrackExecutionTime;
import ru.development.core.httpCore.httpClient.IHttpCoreImpl;
import ru.development.core.mapper.GigaChatModelInfoMapper;
import ru.development.core.model.*;

import org.springframework.http.HttpHeaders;
import ru.development.core.model.ChatResultDto;
import ru.development.core.model.GigaChatResponseDto;
import ru.development.core.repository.GigaChatModelInfoRepository;
import ru.development.infrastructurekafka.model.GigaChatProducerInfo;
import ru.development.infrastructurekafka.service.GigachatProducer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@SuppressWarnings("ALL")
@Slf4j
@Service
@RequiredArgsConstructor
public class GigaChatChatModelService {
    private static final Map<String, AccessToken> accessTokens = new HashMap<>();
    private final IHttpCoreImpl httpCore;
    private final ExecutorService executorService;
    private final GigaChatService gigaChatService;

    private final GigachatProducer gigachatProducer;

    @TrackExecutionTime
    public @NonNull GigaChatResponse sendMessage(HttpServletRequest servletRequest,
                                                 String userRequestId,
                                                 String message) {
        if (isNull(userRequestId)) {
            log.info("Пустой ID запроса пользователя");
            userRequestId = UUID.randomUUID().toString();
            log.info("Теперь ID запроса пользователя: {}", userRequestId);
        }
        final String finalUserRequestId = userRequestId;

        try {
            CompletableFuture<ChatResultDto> chatFuture = CompletableFuture.supplyAsync(() ->
                            checkAccessToken(servletRequest), executorService)
                    .thenCompose(token -> {

                        Map<String, String> contextMap = MDC.getCopyOfContextMap();

                        return CompletableFuture.supplyAsync(() -> {
                            if (nonNull(contextMap)) {
                                MDC.setContextMap(contextMap);
                            }
                            MDC.put("ID запроса пользователя: ", finalUserRequestId);
                            MDC.put("Токен доступа: ", token.getAccessToken());

                            return sendGigaChatMessage(
                                    message,
                                    token.getAccessToken(),
                                    finalUserRequestId);
                        }, executorService);
                    }).exceptionally(ex -> {
                        log.error("[ERROR] Ошибка при получении токена или отправке сообщения: {}", ex.getMessage());
                        throw new RuntimeException(ex.getMessage(), ex);
                    });

            CompletableFuture.runAsync(() -> {
                GigaChatProducerInfo gigaChatProducerInfo = getGigaChatProducerInfo(servletRequest, finalUserRequestId);
                gigachatProducer.sendMessage(gigaChatProducerInfo);
            }, executorService).exceptionally(ex -> {
                log.error("[ERROR] Ошибка отправки данных ProducerFactory: {}", ex.getMessage());
                throw new RuntimeException(ex.getMessage(), ex);
            });

            return chatFuture.join();
        } catch (HttpClientException ex) {
            log.error("Ошибка! Не удалось выполнить запрос: {}", ex.getMessage());
            throw new RuntimeException(ex);
        } finally {
            MDC.clear();
        }
    }

    private ChatResultDto sendGigaChatMessage(
            String message,
            String bearerToken,
            String finalUserRequestId
    ) {
        var client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withProvidedTokenAuth(bearerToken)
                        .build())
                .connectTimeout(10)
                .readTimeout(60)
                .build();

        var response = client.completions(CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT)
                .message(ChatMessage.builder()
                        .content(message)
                        .role(ChatMessageRole.USER)
                        .build())
                .build());

        List<Choice> choices = nonNull(response.choices()) ? response.choices() : Collections.emptyList();
        GigaChatModelInfo modelInfo = gigaChatService.saveChatInfo(choices, response, finalUserRequestId, message, null);

        return ChatResultDto.builder()
                .response(getGigaChatResponseDto(modelInfo))
                .build();
    }

    private static GigaChatProducerInfo getGigaChatProducerInfo(HttpServletRequest servletRequest, String finalUserRequestId) {
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


    public @NonNull AccessToken checkAccessToken(HttpServletRequest servletRequest) {
        String remoteAddr = servletRequest.getRemoteAddr();

        if (accessTokens.containsKey(remoteAddr) && nonNull(remoteAddr)) {
            AccessToken accessToken = accessTokens.get(remoteAddr);
            long millis = System.currentTimeMillis();

            if (accessToken.getExpiresAt() < millis) {
                log.info("[INFO] Token expired after {}", ((millis - accessToken.getExpiresAt()) * 60));
                return accessToken;
            } else {
                accessTokens.remove(remoteAddr);
                return getAccessToken(remoteAddr);
            }
        } else {
            return getAccessToken(remoteAddr);
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

    private AccessToken getAccessToken(String remoteAddr) {
        MultiValueMap<String, String> formData = getMultiValueMap();
        log.info("[INFO] MultiMap {}", formData);
        try {
            ResponseEntity<AccessToken> responseEntity = getAccessTokenResponseEntity(formData);
            accessTokens.put(remoteAddr, responseEntity.getBody());
            return responseEntity.getBody();
        } catch (HttpClientException ex) {
            log.warn("Ошибка! Не удалось выполнить запрос: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private static GigaChatResponseDto getGigaChatResponseDto(GigaChatModelInfo modelInfo) {
        return GigaChatResponseDto.builder().message(modelInfo.message()).modelName(modelInfo.modelName()).gigaChatId(modelInfo.id()).build();
    }

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
