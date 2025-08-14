package ru.development.core.service;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.ModelName;
import chat.giga.model.completion.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.development.core.httpCore.httpClient.IHttpCoreImpl;
import ru.development.core.mapper.GigaChatModelInfoMapper;
import ru.development.core.model.*;

import org.springframework.http.HttpHeaders;
import ru.development.core.repository.GigaChatModelInfoRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class GigaChatChatModelService {
    private static final Map<String, AccessToken> accessTokens = new HashMap<>();
    public static final String ERROR_HTTP_CLIENT_EXCEPTION = "[ERROR] HttpClientException {}";
    private final IHttpCoreImpl httpCore;
    private final GigaChatModelInfoRepository gigaChatModelInfoRepository;
    private final GigaChatModelInfoMapper gigaChatModelInfoMapper;

    @Transactional
    public GigaChatResponse sendMessage(HttpServletRequest servletRequest, String userRequestId, String message) {
        if (isNull(userRequestId)) {
            userRequestId = UUID.randomUUID().toString();
            log.info("userRequestId is {}", userRequestId);
        }
        final String finalUserRequestId = userRequestId;

        try {
            AccessToken token = checkAccessToken(servletRequest);
            String bearerToken = token.getAccessToken();

            MDC.put("userRequestId", userRequestId);
            MDC.put("bearerToken", bearerToken);
            log.info("[INFO] authKey={}", userRequestId);
            log.info("[INFO] bearerToken={}", bearerToken);

            CompletableFuture<GigaChatResponse> future = CompletableFuture.supplyAsync(() ->
                            GigaChatClient.builder()
                                    .authClient(AuthClient.builder()
                                            .withProvidedTokenAuth(bearerToken)
                                            .build()
                                    )
                                    .connectTimeout(5)
                                    .readTimeout(5)
                                    .maxRetriesOnAuthError(3)
                                    .logRequests(true)
                                    .logResponses(true)
                                    .build())
                    .thenCompose(gigaChatClient -> CompletableFuture.supplyAsync(() ->
                                    gigaChatClient.completions(CompletionRequest.builder()
                                            .model(ModelName.GIGA_CHAT)
                                            .message(ChatMessage.builder()
                                                    .content(message)
                                                    .role(ChatMessageRole.USER)
                                                    .build()
                                            ).build()))
                            .handle((response, ex) -> {
                                log.info("[INFO] response={}", response);
                                List<Choice> choices = nonNull(response.choices()) ? response.choices() : Collections.emptyList();
                                String status = isNull(ex) ? "SUCCESS" : ex.getMessage();
                                GigaChatModelInfo modelInfo = saveChatInfo(choices, response, finalUserRequestId, message, status);
                                log.info("[INFO] modelInfo={}", modelInfo);
                                return ChatResultDto.builder()
                                        .response(createResponse("GigaChatResponseDto", modelInfo))
                                        .status(status)
                                        .build();
                            })
                    );

            return future.join();
        } catch (HttpClientException ex) {
            log.warn(ERROR_HTTP_CLIENT_EXCEPTION, ex.getMessage());
            throw new RuntimeException(ex);
        } finally {
            MDC.clear();
        }
    }

    protected GigaChatModelInfo saveChatInfo(
            List<Choice> choices,
            CompletionResponse completions,
            String userRequestId,
            String userMessage,
            String status
    ) {
        ChoiceMessage choice = choices.getFirst().message();
        GigaChatModelInfo entity = gigaChatModelInfoMapper.toEntity(completions.model(), choice.content(), choice.role().name(), userRequestId, userMessage, status);
        gigaChatModelInfoRepository.save(entity);
        return entity;
    }

    private static GigaChatResponse createResponse(String type, GigaChatModelInfo gigaChatModelInfo) {
        return switch (type) {
            case "GigaChatResponseDto" ->
                    new GigaChatResponseDto(gigaChatModelInfo.getModelName(), gigaChatModelInfo.getContent(), gigaChatModelInfo.getId());
            default -> throw new RuntimeException(type);
        };
    }

    public AccessToken checkAccessToken(HttpServletRequest servletRequest) {
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

    private AccessToken getAccessToken(String remoteAddr) {
        MultiValueMap<String, String> formData = getMultiValueMap();
        log.info("[INFO] MultiMap {}", formData);
        try {
            ResponseEntity<AccessToken> responseEntity = getAccessTokenResponseEntity(formData);
            accessTokens.put(remoteAddr, responseEntity.getBody());
            return responseEntity.getBody();
        } catch (HttpClientException ex) {
            log.warn(ERROR_HTTP_CLIENT_EXCEPTION, ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private String formDataToString(MultiValueMap<String, String> formData) {
        return formData.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(value -> encode(entry.getKey()) + "=" + encode(value)))
                .collect(Collectors.joining("&"));
    }

    private String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private ResponseEntity<AccessToken> getAccessTokenResponseEntity(MultiValueMap<String, String> formData) {
        return httpCore.post(
                "https://ngw.devices.sberbank.ru:9443/api/v2/oauth",
                getHeaders(),
                formDataToString(formData),
                AccessToken.class
        );
    }

    private static MultiValueMap<String, String> getMultiValueMap() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("scope", "GIGACHAT_API_PERS");
        return formData;
    }

    public HttpHeaders getHeaders() {
        String clientSecret = "862b853a-7be8-4b84-8f74-44c92991fb85";
        String clientID = "2b16995d-7f48-4823-9fac-21cf15ae08cb";
        String authKey = Base64.getEncoder().encodeToString((clientID + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
        log.info("[INFO] authKey={}", authKey);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + authKey);
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        httpHeaders.add("Accept", "application/json");
        httpHeaders.add("RqUID", UUID.randomUUID().toString());
        log.info("[INFO] httpHeaders={}", httpHeaders);
        return httpHeaders;
    }

}
