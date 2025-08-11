package ru.development.core.service;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.langchain4j.GigaChatChatModel;
import chat.giga.langchain4j.GigaChatChatRequestParameters;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.completion.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.development.core.httpCore.IHttpCoreImpl;
import ru.development.core.model.AccessToken;
import ru.development.core.model.ChatCompletionRequest;

import org.springframework.http.HttpHeaders;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class GigaChatChatModelService {
    private static final Map<String, AccessToken> accessTokens = new HashMap<>();
    private final IHttpCoreImpl httpCore;
//    private String authKey = "MmIxNjk5NWQtN2Y0OC00ODIzLTlmYWMtMjFjZjE1YWUwOGNiOmU5ZGQ4ZWQwLWM0MWYtNGE4Ny1hNDhlLTUwMjY1OGE3NTE5MQ==";

    public String sendMessage(HttpHeaders httpHeaders, String userRequestId, String message) {
        if (isNull(userRequestId)) {
            userRequestId = UUID.randomUUID().toString();
            log.info("userRequestId is {}", userRequestId);
        }

        try {
            AccessToken token = checkAccessToken(userRequestId);
            String bearerToken = token.getAccessToken();

            MDC.put("userRequestId", userRequestId);
            MDC.put("bearerToken", bearerToken);
            log.info("authKey={}", userRequestId);
            log.info("bearerToken={}", bearerToken);

            GigaChatClient gigaChatClient = GigaChatClient.builder()
                    .authClient(AuthClient.builder()
                            .withProvidedTokenAuth(bearerToken)
                            .build())
                    .logRequests(true)
                    .logResponses(true)
                    .build();

            CompletionResponse completions = gigaChatClient.completions(CompletionRequest.builder()
                    .model(ModelName.GIGA_CHAT)
                    .message(ChatMessage.builder()
                            .content(message)
                            .role(ChatMessageRole.USER)
                            .build()
                    )
                    .build()
            );

            List<Choice> choices = completions.choices();

            return choices.getFirst().toString();
        } catch (HttpClientException ex) {
            log.warn("[ERROR] HttpClientException {}", ex.getMessage());
            throw new RuntimeException(ex);
        } finally {
            MDC.clear();
        }
    }

    public AccessToken checkAccessToken(String userRequestId) {
        if (accessTokens.containsKey(userRequestId) && nonNull(userRequestId)) {
            return accessTokens.get(userRequestId);
        } else {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("scope", "GIGACHAT_API_PERS");

            log.info("[INFO] MultiMap {}", formData);
            try {
                ResponseEntity<AccessToken> responseEntity = httpCore.post(
                        "https://ngw.devices.sberbank.ru:9443/api/v2/oauth",
                        getHeaders(),
                        formData,
                        AccessToken.class
                );
                accessTokens.put(userRequestId, responseEntity.getBody());
                return responseEntity.getBody();
            } catch (HttpClientException ex) {
                log.warn("[ERROR] {}", ex.getMessage());
                throw new RuntimeException(ex);
            }
        }
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
