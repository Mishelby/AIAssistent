//package ru.development.main;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import ru.development.core.httpCore.httpClient.Connection;
//import ru.development.core.httpCore.httpClient.HttpClientConnection;
//import ru.development.core.httpCore.httpClient.IHttpCore;
//import ru.development.core.httpCore.httpClient.IHttpCoreImpl;
//import ru.development.core.httpCore.retryPolice.ExponentialRetryPolicy;
//import ru.development.core.httpCore.retryPolice.RetryPolicyProvider;
//
//
//@ExtendWith(MockitoExtension.class)
//class CoreApplicationMockitoTest {
//    private Connection connection;
//    @Mock
//    private IHttpCore httpCore;
//    @Mock
//    private RetryPolicyProvider retryPolicyProvider;
//
//    @BeforeEach
//    void setUp() {
//        retryPolicyProvider = new ExponentialRetryPolicy();
//        httpCore = new IHttpCoreImpl(new ObjectMapper());
//        HttpClientConnection httpClientConnection = new HttpClientConnection.Builder()
//                .iHttpCore(httpCore)
//                .retryTemplate(retryPolicyProvider)
//                .build();
//
//        connection = new Connection(httpClientConnection);
//    }
//
//    @Test
//    void testCass() {
//        TestResponse mockResponse = new TestResponse(
//                "John",
//                "john@test.com",
//                "1234",
//                "2025-09-03",
//                "1"
//        );
//
//        IHttpCore iHttpCore = connection.httpConnection().iHttpCore();
//        ResponseEntity<TestResponse> responseEntity = ResponseEntity.ok(mockResponse);
//
//        Mockito.when(iHttpCore.get(
//                Mockito.anyString(),
//                Mockito.any(),
//                Mockito.eq(TestResponse.class)
//        )).thenReturn(responseEntity);
//
//        HttpHeaders headers = new HttpHeaders();
//
//        headers.add("Content-Type", "application/json");
//        headers.add("Accept", "application/json");
//        ResponseEntity<TestResponse> response = httpCore.get(
//                "https://fake-url/api/v1/users",
//                headers,
//                TestResponse.class
//        );
//
//        Assertions.assertNotNull(response);
//        Assertions.assertNotNull(response.getBody());
//        Assertions.assertEquals("John", response.getBody().name());
//        Assertions.assertEquals("john@test.com", response.getBody().email());
//
//        Mockito.verify(httpCore).get(
//                Mockito.anyString(),
//                Mockito.any(),
//                Mockito.eq(TestResponse.class)
//        );
//    }
//}
