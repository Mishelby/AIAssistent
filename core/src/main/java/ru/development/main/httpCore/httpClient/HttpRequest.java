package ru.development.main.httpCore.httpClient;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@Value
@Builder
@Accessors(fluent = true)
public class HttpRequest {
    HttpMethod method;
    String url;
    Map<String, List<String>> headers;
    byte[] body;

    public static class HttpRequestBuilder {
        Map<String, List<String>> headers = new HashMap<>();

        public HttpRequestBuilder header(String name, String value) {
            this.headers.computeIfAbsent(name, k -> new ArrayList<>(1))
                    .add(value);
            return this;
        }

        public HttpRequestBuilder headerIf(boolean condition, String name, String value) {
            if (condition) {
                header(name, value);
            }
            return this;
        }
        
    }

    public String bodyAsString() {
        if (nonNull(body) && body.length > 0) {
            String result = new String(body, StandardCharsets.UTF_8);
            if (result.startsWith("\"") && result.endsWith("\"")) {
                return result.substring(1, result.length() - 1);
            }
            return result;
        }else{
            return null;
        }
    }

}
