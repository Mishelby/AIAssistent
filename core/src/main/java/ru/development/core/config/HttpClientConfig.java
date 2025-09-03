package ru.development.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.development.core.httpCore.httpClient.Connection;
import ru.development.core.httpCore.httpClient.HttpClientConnectionFactory;
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
                HttpClientConnectionFactory.create(iHttpCoreImpl, retryPolicyProvider)
        );
        log.info("[CONNECTION INFO] DEFAULT CONNECTION {}", connection);
        return connection;
    }

    @Bean
    public Connection connectionWithoutRetry(
            IHttpCoreImpl iHttpCoreImpl
    ) {
        log.info("[CONNECTION INFO] CONNECTION WITHOUT RETRY POLICY");
        Connection connection = new Connection(
                HttpClientConnectionFactory.createWithoutRetry(iHttpCoreImpl)
        );
        log.info("[CONNECTION INFO] CONNECTION WITHOUT RETRY {}", connection);
        return connection;
    }
}
