package ru.development.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.development.core.httpCore.httpClient.IHttpCore;
import ru.development.core.httpCore.httpClient.IHttpCoreImpl;

import java.util.Arrays;

@Slf4j
class CoreApplicationTests {

    private IHttpCore httpCore;

    @BeforeEach
    void setUp() {
        httpCore = new IHttpCoreImpl(new ObjectMapper());
    }

    @Test
    void testGetUsersFromRealApi() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Accept", "application/json");

        ResponseEntity<TestResponse[]> response = httpCore.get(
                "https://68a72b93639c6a54e9a13144.mockapi.io/api/v1/users",
                httpHeaders,
                TestResponse[].class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().length > 0);

       for (TestResponse testResponse : response.getBody()) {
           log.info("[TEST INFO] Response: {}", testResponse);
       }
    }

}
