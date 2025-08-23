package ru.development.main.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.development.core.httpCore.httpClient.HttpClientException;
import ru.development.core.httpCore.httpClient.IHttpCoreImpl;
import ru.development.main.model.AccessToken;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckTokenService {
    // TODO Пока вместо кеша
    private static final Map<String, AccessToken> accessTokens = new HashMap<>();
    private final IHttpCoreImpl httpCore;

    // Проверяю, есть ли токен в кеше, если нет, отправляю запрос на получение нового
    public @NonNull AccessToken checkAccessToken(final String remoteAddr) {
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
        String clientSecret = "998803c3-538e-4ad9-94f6-14951558fa76";
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
