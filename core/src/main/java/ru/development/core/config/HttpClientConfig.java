package ru.development.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.development.core.httpCore.httpClient.Connection;
import ru.development.core.httpCore.httpClient.HttpClientConnection;
import ru.development.core.httpCore.httpClient.IHttpCoreImpl;
import ru.development.core.httpCore.retryPolice.ExponentialRetryPolicy;

@Slf4j
@Configuration
public class HttpClientConfig {

    @Bean
    public Connection defaultConnection(
            IHttpCoreImpl iHttpCoreImpl, ExponentialRetryPolicy exponentialRetryPolicy
    ) {
        return new Connection(
                new HttpClientConnection.Builder()
                        .iHttpCore(iHttpCoreImpl)
                        .restTemplate(exponentialRetryPolicy)
                        .build()
        );
    }
}
