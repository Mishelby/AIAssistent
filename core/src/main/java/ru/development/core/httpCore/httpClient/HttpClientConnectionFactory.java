package ru.development.core.httpCore.httpClient;

import ru.development.core.httpCore.retryPolice.RetryPolicyProvider;

public interface HttpClientConnectionFactory {
    static HttpClientConnection create(IHttpCore httpCore, RetryPolicyProvider retryTemplate) {
        return new HttpClientConnection(httpCore, retryTemplate);
    }

    static HttpClientConnection createWithoutRetry(IHttpCore httpCore) {
        return new HttpClientConnection(httpCore);
    }

    static HttpClientConnection defaultConnection(IHttpCore httpCore, RetryPolicyProvider retryTemplate) {
        return new HttpClientConnection.Builder()
                .iHttpCore(httpCore)
                .retryTemplate(retryTemplate)
                .build();
    }
}
