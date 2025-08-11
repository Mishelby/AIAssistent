package ru.development.core.httpCore;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public interface IHttpCore {
    <T> ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> responseType);
    <T> ResponseEntity<T> post(String url, HttpHeaders headers, Object bodyValue, Class<T> responseType);
    <T> ResponseEntity<T> post(String url, HttpHeaders headers, MultiValueMap<String, String> formData, Class<T> responseType);
}
