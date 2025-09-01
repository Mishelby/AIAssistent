package ru.development.core.httpCore.httpClient;


import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.net.CookieManager;
import java.net.CookiePolicy;

@Slf4j
@Value
@Component
public class Connection {
   HttpClientConnection httpConnection;

    public Connection(IHttpCoreImpl iHttpCoreImpl, RetryTemplate httpRetryTemplate) {
        httpConnection = new HttpClientConnection.Builder()
                .iHttpCore(iHttpCoreImpl)
                .restTemplate(httpRetryTemplate)
                .build();
    }
}
