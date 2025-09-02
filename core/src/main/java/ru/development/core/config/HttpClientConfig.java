package ru.development.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.development.core.httpCore.httpClient.Connection;
import ru.development.core.httpCore.httpClient.HttpClientConnection;
import ru.development.core.httpCore.httpClient.IHttpCoreImpl;
import ru.development.core.httpCore.retryPolice.RetryPolicyProvider;

@Slf4j
@Configuration
public class HttpClientConfig {

    @Bean
    public Connection defaultConnection(
            IHttpCoreImpl iHttpCoreImpl,
            @Qualifier("exponentialRetryPolicy") RetryPolicyProvider retryPolicyProvider
    ) {
        log.info("[CONNECTION INFO] DEFAULT CONNECTION WITH RETRY POLICY");
        Connection connection = new Connection(
                new HttpClientConnection.Builder()
                        .iHttpCore(iHttpCoreImpl)
                        .retryTemplate(retryPolicyProvider)
                        .build()
        );
        log.info("[CONNECTION INFO] {}", connection);
        return connection;
    }
}
