package ru.development.core.httpCore.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Component
public class IHttpCoreImpl implements IHttpCore {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public IHttpCoreImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20L))
//                .authenticator(Authenticator.getDefault())
                .build();

    }

    @Override
    public <T> ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> responseType) {
        String[] headersNames = getHeadersNames(headers);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15L))
                .headers(headersNames)
                .method(HttpMethod.GET.name(), HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response;
        T responseBody;

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            responseBody = objectMapper.readValue(response.body(), responseType);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(response.statusCode())
                .body(responseBody);
    }


    @Override
    public <T> ResponseEntity<T> post(String url, HttpHeaders headers, Object bodyValue, Class<T> responseType) {
        String[] headersNames = getHeadersNames(headers);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15L))
                .headers(headersNames)
                .method(HttpMethod.POST.name(),
                        HttpRequest.BodyPublishers.ofByteArray(bodyValue.toString().getBytes()))
                .build();

        HttpResponse<String> response;
        T responseBody;

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            responseBody = objectMapper.readValue(response.body(), responseType);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(response.statusCode())
                .body(responseBody);

    }

    @Override
    public <T> CompletableFuture<ResponseEntity<T>> postAsync(String url, HttpHeaders headers, Object bodyValue, Class<T> responseType) {
        return null;
    }


    private static String[] getHeadersNames(HttpHeaders headers) {
        return headers.entrySet()
                .stream()
                .flatMap(entry -> entry.getValue()
                        .stream().map(value -> new String[]{entry.getKey(), value}))
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }


}
