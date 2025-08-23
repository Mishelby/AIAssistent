package ru.development.core.httpCore.httpClient;

import chat.giga.http.client.sse.SseListener;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface HttpClient {
    HttpResponse execute(HttpRequest request) throws IOException;

    void execute(HttpRequest request, SseListener listener);

    CompletableFuture<HttpResponse> executeAsync(HttpRequest request);
}
