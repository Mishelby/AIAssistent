package ru.development.core.httpCore.httpClient;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static java.util.Objects.nonNull;

public class HttpClientException extends RuntimeException {
    private int statusCode;
    private byte[] responseBody;

    public HttpClientException(int statusCode, byte[] responseBody) {
        super(String.format("StatusCode: %d, responseBody: %s", statusCode,
                nonNull(responseBody) && responseBody.length > 0 ? Arrays.toString(responseBody) : null)
        );
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int statusCode() {
        return statusCode;
    }

    public byte[] body() {
        return responseBody;
    }

    public String bodyAsString() {
        if (nonNull(responseBody) && responseBody.length > 0) {
            return new String(responseBody, StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }
}
