package ru.development.core.httpCore.httpClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import ru.development.core.httpCore.baseUrlPrefix.BaseUrlPrefix;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;


@Slf4j
@Component
public class IHttpCoreImpl implements IHttpCore {
    private final RestClient restClient;
    private HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public IHttpCoreImpl(@Autowired BaseUrlPrefix baseUrlPrefix, ObjectMapper objectMapper) {
        this.restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .requestInterceptor((request, body, execution) -> {
                    log.info("Raw Request: {} {} {}", request.getMethod(), request.getURI(), request.getHeaders());
                    return execution.execute(request, body);
                })
                .baseUrl(baseUrlPrefix.getBaseUrl())
                .build();

        this.objectMapper = objectMapper;
        httpClient = new HttpClientBuilderImpl()
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
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
