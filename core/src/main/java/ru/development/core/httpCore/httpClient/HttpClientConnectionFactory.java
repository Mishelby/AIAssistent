package ru.development.core.httpCore.httpClient;

import ru.development.core.httpCore.retryPolice.RetryPolicyProvider;

public interface HttpClientConnectionFactory {
    HttpClientConnection create(IHttpCore httpCore, RetryPolicyProvider retryTemplate);
}
