package ru.development.main.httpCore.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@Value
@Builder
@Accessors(fluent = true)
public class HttpResponse {
    int statusCode;
    @Builder.Default
    Map<String, List<String>> headers = new HashMap<>();
    byte[] body;
    ObjectMapper objectMapper = new ObjectMapper();

    public <T> T bodyAsString(Class<T> responseType) {
        if (nonNull(body)) {
            try {
                return objectMapper.readValue(body, responseType);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
