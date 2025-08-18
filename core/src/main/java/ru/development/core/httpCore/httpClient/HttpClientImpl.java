package ru.development.core.httpCore.httpClient;

import chat.giga.http.client.sse.SseListener;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest.BodyPublisher;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.nonNull;

@Slf4j
public class HttpClientImpl implements HttpClient {
    private final java.net.http.HttpClient httpClient;
    private final Duration readTimeout;
    private final Map<String, String> defaultHeaders;

    public HttpClientImpl(HttpClientBuilderImpl builder) {
        var httpClientBuilder = builder.httpClientBuilder();
        if (nonNull(builder.connectTimeout())) {
            httpClientBuilder.connectTimeout(builder.connectTimeout());
        }

        if (nonNull(builder.customHeaders()) && !builder.customHeaders().isEmpty()) {
            this.defaultHeaders = builder.customHeaders();
        } else {
            this.defaultHeaders = Map.of();
        }

        java.net.http.HttpClient baseClient = httpClientBuilder.build();

        if (nonNull(builder.decorator())) {
            this.httpClient = builder.decorator().apply(baseClient);
        } else {
            this.httpClient = baseClient;
        }

        this.readTimeout = builder.readTimeout();
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        HttpResponse response;
        try {
            java.net.http.HttpRequest httpRequest = mapRequest(request);
            var httpResponse = httpClient.send(
                    httpRequest,
                    java.net.http.HttpResponse.BodyHandlers.ofByteArray()
            );

            if (!isSuccessful(httpResponse)) {
                throw getException(httpResponse);
            }

            response = mapResponse(httpResponse).orElse(null);

        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        return response;
    }


    @Override
    public void execute(HttpRequest request, SseListener listener) {

    }

    @Override
    public CompletableFuture<HttpResponse> executeAsync(HttpRequest request) {
        return httpClient.sendAsync(mapRequest(request),
                        java.net.http.HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(result -> {
                    if (isSuccessful(result)) {
                        throw getException(result);
                    }

                    return mapResponse(result)
                            .orElse(null);
                });
    }

    private HttpClientException getException(java.net.http.HttpResponse<byte[]> response) {
        throw new HttpClientException(response.statusCode(), response.body());
    }

    private Optional<HttpResponse> mapResponse(java.net.http.HttpResponse<byte[]> response) {
        HttpResponse result = null;
        if (nonNull(response)) {
            result = HttpResponse.builder()
                    .statusCode(response.statusCode())
                    .headers(response.headers().map())
                    .body(response.body())
                    .build();
        }

        return Optional.ofNullable(result);
    }

    private static boolean isSuccessful(java.net.http.HttpResponse<?> response) {
        int statusCode = response.statusCode();
        return statusCode >= 200 && statusCode < 300;
    }



    private java.net.http.HttpRequest mapRequest(HttpRequest request) {
        try {
            log.info("[INFO] HttpRequest info: {}", request.toString());
            var builder = java.net.http.HttpRequest.newBuilder()
                    .uri(new URI(request.url()));

            if (nonNull(request.headers())) {
                request.headers().forEach((name, values) -> {
                    if (nonNull(values)) {
                        values.forEach(value -> builder.header(name, value));
                    }
                });
            }

            if (nonNull(defaultHeaders)) {
                defaultHeaders.forEach(builder::header);
            }

            BodyPublisher bodyPublisher;
            log.info("[INFO] Body publisher info: {}", request.bodyAsString());
            if (nonNull(request.body())) {
                bodyPublisher = java.net.http.HttpRequest.BodyPublishers.ofString(request.bodyAsString());
            } else {
                bodyPublisher = java.net.http.HttpRequest.BodyPublishers.noBody();
            }

            builder.method(request.method().name(), bodyPublisher);
            if (nonNull(readTimeout)) {
                builder.timeout(readTimeout);
            }

            return builder.build();
        } catch (URISyntaxException  e) {
            throw new RuntimeException(e);
        }
    }
}
