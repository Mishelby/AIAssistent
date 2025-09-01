package ru.development.core.httpCore.httpClient;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.concurrent.ExecutorService;

import static java.util.Objects.nonNull;


@Slf4j
@Component
public final class IHttpCoreImpl implements IHttpCore {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ExecutorService coreExecutorService;

    public IHttpCoreImpl(ObjectMapper objectMapper, ExecutorService executorService) {
        this.objectMapper = objectMapper;
        this.coreExecutorService = executorService;
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20L))
                .build();

    }

    @Override
    public <T> ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> responseType) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15L))
                    .headers(getHeadersNames(headers))
                    .method(HttpMethod.GET.name(), HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            T responseBody = objectMapper.readValue(response.body(), responseType);
            return ResponseEntity.status(response.statusCode())
                    .body(responseBody);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }


    @Override
    public <T> ResponseEntity<T> post(String url, HttpHeaders headers, Object bodyValue, Class<T> responseType) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15L))
                    .headers(getHeadersNames(headers))
                    .method(HttpMethod.POST.name(),
                            HttpRequest.BodyPublishers.ofByteArray(bodyValue.toString().getBytes()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            T responseBody = objectMapper.readValue(response.body(), responseType);
            return ResponseEntity.status(response.statusCode())
                    .body(responseBody);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> CompletableFuture<ResponseEntity<T>> postAsync(
            String url, HttpHeaders headers, Object bodyValue, Class<T> responseType
    ) {
        String[] headersNames = getHeadersNames(headers);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15L))
                .headers(headersNames)
                .method(HttpMethod.POST.name(),
                        HttpRequest.BodyPublishers.ofByteArray(bodyValue.toString().getBytes()))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(httpResponse -> {
                    log.info("HTTP response info: statusCode={}, body={}",
                            httpResponse.statusCode(), httpResponse.body());
                    try {
                        T responseBody = objectMapper.readValue(httpResponse.body(), responseType);
                        if (httpResponse.statusCode() >= 400) {
                            throw new HttpClientException(httpResponse.statusCode(), httpResponse.body().getBytes());
                        }
                        return ResponseEntity.status(httpResponse.statusCode()).body(responseBody);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error parsing response body", e);
                    }
                }).exceptionally(ex -> {
                    Throwable cause = nonNull(ex.getCause()) ? ex.getCause() : ex;
                    if (cause instanceof HttpClientException httpClientException) {
                        throw httpClientException;
                    } else {
                        throw new RuntimeException(cause);
                    }
                });
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
