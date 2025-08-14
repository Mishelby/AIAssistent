package ru.development.core.httpCore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import ru.development.core.httpCore.baseUrlPrefix.BaseUrlPrefix;


@Slf4j
@Component
public class IHttpCoreImpl implements IHttpCore {
    private final RestClient restClient;

    public IHttpCoreImpl(@Autowired BaseUrlPrefix baseUrlPrefix) {
        this.restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .requestInterceptor((request, body, execution) -> {
                    log.info("Raw Request: {} {} {}", request.getMethod(), request.getURI(), request.getHeaders());
                    return execution.execute(request, body);
                })
                .baseUrl(baseUrlPrefix.getBaseUrl())
                .build();
    }

    @Override
    public <T> ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> responseType) {
        return restClient.get()
                .uri(url)
                .headers(h -> h.addAll(headers))
                .retrieve()
                .toEntity(responseType);
    }

    @Override
    public <T> ResponseEntity<T> post(String url, HttpHeaders headers, Object bodyValue, Class<T> responseType) {
        return null;
    }

    @Override
    public <T> ResponseEntity<T> post(String url, HttpHeaders headers, MultiValueMap<String, String> formData, Class<T> responseType) {
        return restClient.post()
                .uri(url)
                .headers(h -> h.addAll(headers))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .toEntity(responseType);
    }

}
