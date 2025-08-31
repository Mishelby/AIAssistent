package ru.development.core.httpCore.httpClient;

import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

import static java.util.Objects.nonNull;

@Getter
@Setter
public class HttpClientException extends RuntimeException {
    private int statusCode;
    private byte[] body;

    public HttpClientException(int statusCode, byte[] body) {
        super(String.format("HTTP Error exception, status code=%s, body=%s", statusCode,
                nonNull(body) && body.length > 0 ? new String(body, StandardCharsets.UTF_8) : null));
    }

    public String bodyAsString() {
        if (nonNull(body) && body.length > 0) {
            return new String(body, StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }
}
