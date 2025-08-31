package ru.development.core.httpCore.httpClient;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

public interface IHttpCore {
    <T> ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> responseType);
    <T> ResponseEntity<T> post(String url, HttpHeaders headers, Object bodyValue, Class<T> responseType);
    <T> CompletableFuture<ResponseEntity<T>> postAsync(String url, HttpHeaders headers, Object bodyValue, Class<T> responseType);
}
