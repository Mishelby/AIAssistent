package ru.development.core.httpCore.httpClient;

import chat.giga.http.client.sse.SseListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static java.util.Objects.nonNull;


@Slf4j
@Component
public class IHttpCoreImpl implements IHttpCore {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public IHttpCoreImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        httpClient = new HttpClientBuilderImpl()
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(30))
                .decorator(null)
                .build();
    }

    @Override
    public <T extends HttpClient> ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> responseType) {
        Map<String, List<String>> headerMap = new HashMap<>(headers);
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, url, headerMap, null);
        HttpResponse response = null;

        try {
            response = httpClient.execute(httpRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(response.statusCode())
                .body(response.bodyAsString(responseType));
    }


    @Override
    public <T> ResponseEntity<T> post(String url, HttpHeaders headers, Object bodyValue, Class<T> responseType) {
        Map<String, List<String>> headerMap = new HashMap<>(headers);
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, url, headerMap, encodeFormData(bodyValue));

        HttpResponse response = null;
        T jsonResponseBody = null;

        try {
            response = httpClient.execute(httpRequest);
            jsonResponseBody = objectMapper.readValue(response.body(), responseType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(response.statusCode())
                .body(jsonResponseBody);
    }

    // TODO Пока не работает
    private Function<HttpClient, HttpClient> decoratorClient = client -> new HttpClient() {
        @Override
        public HttpResponse execute(HttpRequest httpRequest) throws IOException {
            try {
                return httpClient.execute(httpRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void execute(HttpRequest request, SseListener listener) {

        }

        @Override
        public CompletableFuture<HttpResponse> executeAsync(HttpRequest request) {
            return null;
        }
    };

    private byte[] encodeFormData(Object bodyValue) {
        if (nonNull(bodyValue)) {
            try {
                return objectMapper.writeValueAsBytes(bodyValue);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private byte[] getValueAsBytes(MultiValueMap<String, String> formData) {
        try {
            return objectMapper.writeValueAsBytes(formData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
